package com.codergang.chatdirecto.monetization

import android.content.Context
import com.codergang.chatdirecto.data.preferences.MonetizationPreferences
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RevenueCatState(
    val isLoading: Boolean = false,
    val isConfigured: Boolean = Purchases.isConfigured,
    val isProActive: Boolean = false,
    val currentOffering: Offering? = null,
    val customerInfo: CustomerInfo? = null,
    val errorMessage: String? = null
)

class RevenueCatManager(
    context: Context,
    private val onEntitlementChanged: (Boolean) -> Unit,
    private val onMessage: (String) -> Unit
) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _state = MutableStateFlow(RevenueCatState())
    val state: StateFlow<RevenueCatState> = _state.asStateFlow()

    init {
        if (Purchases.isConfigured) {
            Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
                applyCustomerInfo(customerInfo)
            }
        }
    }

    fun refreshAll() {
        if (!ensureConfigured()) return
        refreshCustomerInfo()
        refreshOfferings()
    }

    fun refreshCustomerInfo() {
        val purchases = configuredPurchasesOrNull() ?: return
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { purchases.awaitCustomerInfo() }
                .onSuccess { customerInfo ->
                    applyCustomerInfo(customerInfo)
                }
                .onFailure { throwable ->
                    updateError("Unable to load purchases: ${throwable.message.orEmpty()}")
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun refreshOfferings() {
        val purchases = configuredPurchasesOrNull() ?: return
        scope.launch {
            runCatching { purchases.awaitOfferings() }
                .onSuccess { offerings ->
                    _state.update {
                        it.copy(
                            currentOffering = offerings.current,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    updateError("Unable to load products: ${throwable.message.orEmpty()}")
                }
        }
    }

    fun restorePurchases() {
        val purchases = configuredPurchasesOrNull() ?: return
        scope.launch {
            runCatching { purchases.awaitRestore() }
                .onSuccess { customerInfo ->
                    applyCustomerInfo(customerInfo)
                    postMessage("Purchases restored.")
                }
                .onFailure { throwable ->
                    updateError("Restore failed: ${throwable.message.orEmpty()}")
                }
        }
    }

    fun applyPaywallCustomerInfo(customerInfo: CustomerInfo) {
        applyCustomerInfo(customerInfo)
    }

    fun clear() {
        if (Purchases.isConfigured) {
            Purchases.sharedInstance.removeUpdatedCustomerInfoListener()
        }
        scope.cancel()
    }

    private fun applyCustomerInfo(customerInfo: CustomerInfo) {
        val proActive = isProEntitled(customerInfo)
        MonetizationPreferences.setAdsRemoved(appContext, proActive)
        _state.update {
            it.copy(
                isProActive = proActive,
                customerInfo = customerInfo,
                errorMessage = null
            )
        }
        scope.launch(Dispatchers.Main.immediate) {
            onEntitlementChanged(proActive)
        }
    }

    fun isProEntitled(customerInfo: CustomerInfo?): Boolean {
        return customerInfo
            ?.entitlements
            ?.get(RevenueCatConfig.PRO_ENTITLEMENT_ID)
            ?.isActive == true
    }

    private fun updateError(message: String) {
        _state.update {
            it.copy(errorMessage = message)
        }
        postMessage(message)
    }

    private fun postMessage(message: String) {
        scope.launch(Dispatchers.Main.immediate) {
            onMessage(message)
        }
    }

    private fun ensureConfigured(): Boolean {
        return if (!Purchases.isConfigured) {
            _state.update {
                it.copy(
                    isConfigured = false,
                    errorMessage = "Purchases are unavailable. Configure RevenueCat key and products."
                )
            }
            postMessage("Purchases unavailable. Set RevenueCat key/products first.")
            false
        } else {
            _state.update { it.copy(isConfigured = true) }
            true
        }
    }

    private fun configuredPurchasesOrNull(): Purchases? {
        return if (ensureConfigured()) Purchases.sharedInstance else null
    }
}
