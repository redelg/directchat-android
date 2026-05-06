package com.codergang.chatdirecto.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.runtime.mutableStateOf
import com.codergang.chatdirecto.R
import com.codergang.chatdirecto.data.preferences.MonetizationPreferences
import com.codergang.chatdirecto.monetization.ConsentManager
import com.codergang.chatdirecto.monetization.InterstitialAdManager
import com.codergang.chatdirecto.monetization.MonetizationUiConfig
import com.codergang.chatdirecto.monetization.RevenueCatManager
import com.codergang.chatdirecto.monetization.RewardedAdManager
import com.revenuecat.purchases.Purchases
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainComposeViewModel by viewModels()

    private val proEntitlementState = mutableStateOf(false)
    private val premiumUnlockedState = mutableStateOf(false)
    private val revenueCatReadyState = mutableStateOf(false)

    private lateinit var revenueCatManager: RevenueCatManager
    private lateinit var rewardedAdManager: RewardedAdManager
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val persistedProState = MonetizationPreferences.isAdsRemoved(this)
        proEntitlementState.value = persistedProState
        premiumUnlockedState.value = persistedProState || MonetizationPreferences.isPremiumUnlocked(this)
        revenueCatReadyState.value = Purchases.isConfigured

        if (MonetizationUiConfig.PURCHASES_ENABLED && Purchases.isConfigured) {
            revenueCatManager = RevenueCatManager(
                context = this,
                onEntitlementChanged = { isProActive ->
                    proEntitlementState.value = isProActive
                    premiumUnlockedState.value = isProActive || MonetizationPreferences.isPremiumUnlocked(this)
                },
                onMessage = ::showToast
            )
            revenueCatManager.refreshAll()

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    revenueCatManager.state.collect { state ->
                        proEntitlementState.value = state.isProActive
                        premiumUnlockedState.value = state.isProActive || MonetizationPreferences.isPremiumUnlocked(this@MainActivity)
                        revenueCatReadyState.value = state.isConfigured
                    }
                }
            }

            rewardedAdManager = RewardedAdManager(
                context = this,
                adUnitId = getString(R.string.ad_rewarded_unlock)
            )
            rewardedAdManager.preload()

            interstitialAdManager = InterstitialAdManager(
                context = this,
                adUnitId = getString(R.string.ad_interstitial_chat)
            )
            interstitialAdManager.preload()
        }

        ConsentManager.requestConsentIfNeeded(activity = this) {}

        setContent {
            DirectChatTheme {
                DirectChatRoot(
                    viewModel = viewModel,
                    adsRemoved = proEntitlementState.value,
                    premiumUnlocked = premiumUnlockedState.value,
                    revenueCatReady = revenueCatReadyState.value,
                    onShowMessage = ::showToast,
                    onRefreshPurchases = {
                        if (MonetizationUiConfig.PURCHASES_ENABLED && ::revenueCatManager.isInitialized) {
                            revenueCatManager.refreshCustomerInfo()
                        }
                    },
                    onRestorePurchase = {
                        if (MonetizationUiConfig.PURCHASES_ENABLED && ::revenueCatManager.isInitialized) {
                            revenueCatManager.restorePurchases()
                        }
                    },
                    onPaywallCustomerInfoUpdated = { customerInfo ->
                        if (MonetizationUiConfig.PURCHASES_ENABLED && ::revenueCatManager.isInitialized) {
                            revenueCatManager.applyPaywallCustomerInfo(customerInfo)
                        }
                    },
                    onInterstitialChatOpened = {
                        if (MonetizationUiConfig.PURCHASES_ENABLED && ::interstitialAdManager.isInitialized) {
                            val adsEnabled = MonetizationUiConfig.areAdsEnabled(proEntitlementState.value)
                            interstitialAdManager.onChatOpened(this, adsEnabled)
                        }
                    },
                    onRewardedUnlock = {
                        if (MonetizationUiConfig.PURCHASES_ENABLED && ::rewardedAdManager.isInitialized) {
                            rewardedAdManager.show(
                                activity = this,
                                onRewarded = {
                                    MonetizationPreferences.unlockPremiumForHours(this, 24)
                                    premiumUnlockedState.value =
                                        proEntitlementState.value || MonetizationPreferences.isPremiumUnlocked(this)
                                    showToast(getString(R.string.text_premium_unlocked))
                                },
                                onUnavailable = {
                                    showToast(getString(R.string.text_rewarded_not_ready))
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        if (::revenueCatManager.isInitialized) {
            revenueCatManager.clear()
        }
        super.onDestroy()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
