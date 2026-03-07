package com.codergang.chatdirecto.data.preferences

import android.content.Context
import androidx.core.content.edit

object MonetizationPreferences {

    private const val PREFERENCES_NAME = "PREF_MONETIZATION"
    private const val KEY_ADS_REMOVED = "KEY_ADS_REMOVED"
    private const val KEY_PREMIUM_UNLOCK_UNTIL = "KEY_PREMIUM_UNLOCK_UNTIL"

    fun isAdsRemoved(context: Context): Boolean {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ADS_REMOVED, false)
    }

    fun setAdsRemoved(context: Context, removed: Boolean) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_ADS_REMOVED, removed)
            }
    }

    fun unlockPremiumForHours(context: Context, hours: Int) {
        val now = System.currentTimeMillis()
        val currentUntil = premiumUnlockUntil(context)
        val base = if (currentUntil > now) currentUntil else now
        val unlockUntil = base + hours * 60L * 60L * 1000L
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit {
                putLong(KEY_PREMIUM_UNLOCK_UNTIL, unlockUntil)
            }
    }

    fun isPremiumUnlocked(context: Context): Boolean {
        return premiumUnlockUntil(context) > System.currentTimeMillis()
    }

    fun premiumUnlockUntil(context: Context): Long {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_PREMIUM_UNLOCK_UNTIL, 0L)
    }
}
