package com.codergang.chatdirecto.monetization

object MonetizationUiConfig {
    // If true, ads are always displayed even for entitled users.
    const val FORCE_SHOW_ADS = false

    // Controls purchase-related UI and logic.
    const val PURCHASES_ENABLED = true

    fun areAdsEnabled(adsRemoved: Boolean): Boolean {
        return FORCE_SHOW_ADS || !adsRemoved
    }
}
