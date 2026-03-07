package com.codergang.chatdirecto.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.codergang.chatdirecto.BuildConfig
import com.codergang.chatdirecto.R
import com.codergang.chatdirecto.data.entity.MessageDB
import com.codergang.chatdirecto.data.preferences.SendLimitPreferences
import com.codergang.chatdirecto.data.preferences.UserPreferences
import com.codergang.chatdirecto.data.preferences.Usuario
import com.codergang.chatdirecto.monetization.MonetizationUiConfig

private data class LegalContent(
    val titleRes: Int,
    val assetFileName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DirectChatRoot(
    viewModel: MainComposeViewModel,
    adsRemoved: Boolean,
    premiumUnlocked: Boolean,
    revenueCatReady: Boolean,
    onShowMessage: (String) -> Unit,
    onRefreshPurchases: () -> Unit,
    onRestorePurchase: () -> Unit,
    onPaywallCustomerInfoUpdated: (CustomerInfo) -> Unit,
    onRewardedUnlock: () -> Unit,
    onInterstitialChatOpened: () -> Unit
) {
    val context = LocalContext.current
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val adsEnabled = MonetizationUiConfig.areAdsEnabled(adsRemoved)
    val isPro = adsRemoved || premiumUnlocked

    var showOnboarding by rememberSaveable { mutableStateOf(UserPreferences.get(context) == null) }
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Chat) }
    var pendingNumber by remember { mutableStateOf<String?>(null) }
    var pendingMessage by remember { mutableStateOf<String?>(null) }
    var legalContent by remember { mutableStateOf<LegalContent?>(null) }
    var editingMessage by remember { mutableStateOf<MessageDB?>(null) }
    var creatingMessage by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }
    var showQrScreen by remember { mutableStateOf(false) }
    var showLinkScreen by remember { mutableStateOf(false) }
    var showCategoryManagement by remember { mutableStateOf(false) }
    var currentPhoneCode by remember { mutableStateOf("") }
    var currentPhoneNumber by remember { mutableStateOf("") }
    var currentMessage by remember { mutableStateOf("") }

    fun openPaywall() {
        if (revenueCatReady) {
            showPaywall = true
        } else {
            onShowMessage(context.getString(R.string.text_purchases_unavailable))
        }
    }

    when {
        legalContent != null -> {
            val legal = legalContent!!
            BackHandler { legalContent = null }
            LegalScreen(
                title = stringResource(legal.titleRes),
                assetFileName = legal.assetFileName,
                onBack = { legalContent = null }
            )
        }

        showPaywall && MonetizationUiConfig.PURCHASES_ENABLED && revenueCatReady -> {
            BackHandler { showPaywall = false; onRefreshPurchases() }
            RevenueCatPaywallScreen(
                onDismiss = {
                    showPaywall = false
                    onRefreshPurchases()
                },
                onPurchaseCompleted = { customerInfo ->
                    onPaywallCustomerInfoUpdated(customerInfo)
                    onShowMessage(context.getString(R.string.text_purchase_completed))
                    showPaywall = false
                },
                onRestoreCompleted = { customerInfo ->
                    onPaywallCustomerInfoUpdated(customerInfo)
                    onShowMessage(context.getString(R.string.text_purchases_restored))
                    showPaywall = false
                },
                onPurchaseError = { errorMessage ->
                    onShowMessage(errorMessage)
                }
            )
        }

        showOnboarding -> {
            OnboardingScreen(
                onFinish = {
                    UserPreferences.set(context, Usuario())
                    showOnboarding = false
                }
            )
        }

        else -> {
            MainScaffold(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                topBarActions = {
                    if (selectedTab == MainTab.Chat) {
                        IconButton(onClick = { showLinkScreen = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = stringResource(R.string.text_share_link),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = {
                            if (!isPro) {
                                onShowMessage(context.getString(R.string.text_qr_pro_only))
                                openPaywall()
                            } else {
                                showQrScreen = true
                            }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_qr_code_24),
                                contentDescription = stringResource(R.string.text_generate_qr),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "tabFadeTransition"
                ) { tab ->
                    when (tab) {
                        MainTab.Chat -> ChatTab(
                            adsEnabled = adsEnabled,
                            isPro = isPro,
                            recentChats = chats,
                            favoriteMessages = messages,
                            pendingNumber = pendingNumber,
                            pendingMessage = pendingMessage,
                            onConsumePendingNumber = { pendingNumber = null },
                            onConsumePendingMessage = { pendingMessage = null },
                            onShowMessage = onShowMessage,
                            onOpenChat = { countryCode, numberInput, messageInput ->
                                val cleanNumber = numberInput.filter { it.isDigit() }
                                if (cleanNumber.isEmpty()) {
                                    onShowMessage(context.getString(R.string.text_enter_phone_number))
                                    return@ChatTab
                                }
                                if (!isPro && !SendLimitPreferences.canSend(context)) {
                                    onShowMessage(
                                        context.getString(
                                            R.string.text_daily_limit_reached,
                                            SendLimitPreferences.FREE_DAILY_LIMIT
                                        )
                                    )
                                    openPaywall()
                                    return@ChatTab
                                }
                                val fullNumber = "$countryCode$cleanNumber"
                                val opened = openChatLink(context, fullNumber, messageInput)
                                if (opened) {
                                    if (!isPro) {
                                        SendLimitPreferences.recordSend(context)
                                    }
                                    viewModel.saveChat(
                                        numberWithCode = fullNumber,
                                        formattedNumber = "+$countryCode ${formatPhoneNumber(cleanNumber)}",
                                        numberWithoutCode = cleanNumber
                                    )
                                    onInterstitialChatOpened()
                                } else {
                                    onShowMessage(context.getString(R.string.text_no_compatible_chat_app))
                                }
                            },
                            onCurrentInputChanged = { phoneCode, phoneNumber, message ->
                                currentPhoneCode = phoneCode
                                currentPhoneNumber = phoneNumber
                                currentMessage = message
                            }
                        )

                        MainTab.History -> HistoryTab(
                            chats = chats,
                            adsEnabled = adsEnabled,
                            isPro = isPro,
                            onReuse = { chat ->
                                pendingNumber = chat.numberWithoutCode
                                selectedTab = MainTab.Chat
                            },
                            onDelete = { chat -> viewModel.deleteChat(chat) },
                            onShare = { chat ->
                                shareText(context, "https://wa.me/${chat.number}")
                            },
                            onUpgradeToPro = ::openPaywall
                        )

                        MainTab.Messages -> MessagesTab(
                            messages = messages,
                            categories = categories,
                            selectedCategoryId = selectedCategoryId,
                            isPro = isPro,
                            onCategorySelected = { viewModel.selectCategory(it) },
                            onCreateMessage = {
                                val canCreate = if (!MonetizationUiConfig.PURCHASES_ENABLED) {
                                    true
                                } else {
                                    adsRemoved || premiumUnlocked || messages.size < FREE_TEMPLATE_LIMIT
                                }
                                if (canCreate) {
                                    creatingMessage = true
                                } else {
                                    showPremiumDialog = true
                                }
                            },
                            onEditMessage = { message ->
                                editingMessage = message
                            },
                            onDeleteMessage = { message ->
                                viewModel.deleteMessage(message)
                            },
                            onShareMessage = { message ->
                                shareText(context, message.content)
                            },
                            onUseInChat = { message ->
                                pendingMessage = message.content
                                selectedTab = MainTab.Chat
                            },
                            onToggleFavorite = { message ->
                                viewModel.toggleFavorite(message, isPro) {
                                    onShowMessage(
                                        context.getString(
                                            R.string.text_favorites_limit,
                                            MainComposeViewModel.FREE_FAVORITE_LIMIT
                                        )
                                    )
                                    openPaywall()
                                }
                            },
                            onLockedCategory = {
                                onShowMessage(context.getString(R.string.text_category_pro_only))
                                openPaywall()
                            }
                        )

                        MainTab.Settings -> SettingsTab(
                            adsRemoved = adsRemoved,
                            premiumUnlocked = premiumUnlocked,
                            showMonetization = MonetizationUiConfig.PURCHASES_ENABLED,
                            revenueCatReady = revenueCatReady,
                            onManageCategories = { showCategoryManagement = true },
                            onRateApp = { openStoreDetails(context) },
                            onShareApp = {
                                shareText(
                                    context,
                                    "${context.getString(R.string.text_download_app)}\n\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                                )
                            },
                            onOtherApps = {
                                openUrl(context, "https://play.google.com/store/apps/dev?id=7102806663778142225")
                            },
                            onPrivacyPolicy = {
                                legalContent = LegalContent(
                                    titleRes = R.string.text_privacy_policy,
                                    assetFileName = "privacy_policy.html"
                                )
                            },
                            onTerms = {
                                legalContent = LegalContent(
                                    titleRes = R.string.text_terms_and_conditions,
                                    assetFileName = "terms_and_conditions.html"
                                )
                            },
                            onRestorePurchases = onRestorePurchase,
                            onOpenPaywall = ::openPaywall
                        )
                    }
                }
            }
        }
    }

    if (showQrScreen) {
        ModalBottomSheet(
            onDismissRequest = { showQrScreen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            QrSheetContent(
                phoneCode = currentPhoneCode,
                phoneNumber = currentPhoneNumber,
                initialMessage = currentMessage,
                adsEnabled = adsEnabled
            )
        }
    }

    if (showLinkScreen) {
        ModalBottomSheet(
            onDismissRequest = { showLinkScreen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            LinkSheetContent(
                phoneCode = currentPhoneCode,
                phoneNumber = currentPhoneNumber,
                initialMessage = currentMessage,
                adsEnabled = adsEnabled
            )
        }
    }

    if (creatingMessage) {
        MessageEditorDialog(
            title = stringResource(R.string.text_new_message),
            initialTitle = "",
            initialContent = "",
            categories = categories,
            initialCategoryId = selectedCategoryId ?: 1,
            isPro = isPro,
            onDismiss = { creatingMessage = false },
            onSave = { title, content, categoryId ->
                if (title.isBlank()) {
                    onShowMessage(context.getString(R.string.text_enter_title))
                    return@MessageEditorDialog
                }
                if (content.isBlank()) {
                    onShowMessage(context.getString(R.string.text_enter_your_message))
                    return@MessageEditorDialog
                }
                viewModel.saveMessage(title, content, categoryId)
                creatingMessage = false
            }
        )
    }

    if (editingMessage != null) {
        val message = editingMessage!!
        MessageEditorDialog(
            title = stringResource(R.string.text_edit_message),
            initialTitle = message.title,
            initialContent = message.content,
            categories = categories,
            initialCategoryId = message.categoryId,
            isPro = isPro,
            onDismiss = { editingMessage = null },
            onSave = { title, content, categoryId ->
                if (title.isBlank()) {
                    onShowMessage(context.getString(R.string.text_enter_title))
                    return@MessageEditorDialog
                }
                if (content.isBlank()) {
                    onShowMessage(context.getString(R.string.text_enter_your_message))
                    return@MessageEditorDialog
                }
                viewModel.updateMessage(message, title, content, categoryId)
                editingMessage = null
            },
            onDelete = {
                viewModel.deleteMessage(message)
                editingMessage = null
            }
        )
    }

    if (showCategoryManagement) {
        ModalBottomSheet(
            onDismissRequest = { showCategoryManagement = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CategoryManagementSheetContent(
                categories = categories,
                onAddCategory = { viewModel.addCategory(it) },
                onRenameCategory = { cat, name -> viewModel.renameCategory(cat, name) },
                onDeleteCategory = { viewModel.deleteCategory(it) },
                onShowMessage = onShowMessage
            )
        }
    }

    if (showPremiumDialog && MonetizationUiConfig.PURCHASES_ENABLED) {
        AlertDialog(
            onDismissRequest = { showPremiumDialog = false },
            title = {
                Text(text = stringResource(R.string.text_premium_feature))
            },
            text = {
                Text(text = stringResource(R.string.text_premium_feature_description))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog = false
                        onRewardedUnlock()
                    }
                ) {
                    Text(text = stringResource(R.string.text_watch_ad_unlock))
                }
            },
            dismissButton = {
                androidx.compose.foundation.layout.Row {
                    TextButton(
                        onClick = {
                            showPremiumDialog = false
                            openPaywall()
                        }
                    ) {
                        Text(text = stringResource(R.string.text_buy_remove_ads))
                    }
                    TextButton(onClick = { showPremiumDialog = false }) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@Composable
private fun RevenueCatPaywallScreen(
    onDismiss: () -> Unit,
    onPurchaseCompleted: (CustomerInfo) -> Unit,
    onRestoreCompleted: (CustomerInfo) -> Unit,
    onPurchaseError: (String) -> Unit
) {
    Paywall(
        options = PaywallOptions.Builder(dismissRequest = onDismiss)
            .setShouldDisplayDismissButton(true)
            .setListener(
                object : PaywallListener {
                    override fun onPurchaseCompleted(
                        customerInfo: CustomerInfo,
                        storeTransaction: StoreTransaction
                    ) {
                        onPurchaseCompleted(customerInfo)
                    }

                    override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                        onRestoreCompleted(customerInfo)
                    }

                    override fun onPurchaseError(error: PurchasesError) {
                        onPurchaseError(error.message)
                    }

                    override fun onPurchaseCancelled() {
                        // no-op
                    }

                    override fun onRestoreStarted() {
                        // no-op
                    }

                    override fun onRestoreError(error: PurchasesError) {
                        onPurchaseError(error.message)
                    }
                }
            )
            .build()
    )
}
