package com.codergang.chatdirecto.monetization

import com.codergang.chatdirecto.BuildConfig

object RevenueCatConfig {
    val PUBLIC_SDK_KEY: String
        get() = BuildConfig.RC_API_KEY

    val PRO_ENTITLEMENT_ID: String
        get() = BuildConfig.RC_ENTITLEMENT_PRO

    val LIFETIME_PRODUCT_ID: String
        get() = BuildConfig.RC_PRODUCT_LIFETIME

    fun isPublicKeyConfigured(): Boolean {
        val key = PUBLIC_SDK_KEY.trim()
        return key.isNotEmpty() &&
            !key.equals("REPLACE_WITH_REVENUECAT_ANDROID_PUBLIC_SDK_KEY", ignoreCase = true) &&
            !key.startsWith("REPLACE_") &&
            (key.startsWith("goog_") || key.startsWith("test_"))
    }
}
