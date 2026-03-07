package com.codergang.chatdirecto.data.preferences

import android.content.Context
import androidx.core.content.edit
import java.util.Calendar

object SendLimitPreferences {

    private const val PREFERENCES_NAME = "PREF_SEND_LIMIT"
    private const val KEY_SEND_COUNT = "KEY_SEND_COUNT"
    private const val KEY_SEND_DATE = "KEY_SEND_DATE"

    const val FREE_DAILY_LIMIT = 10

    private fun todayKey(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    fun getSendsToday(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_SEND_DATE, null)
        return if (savedDate == todayKey()) prefs.getInt(KEY_SEND_COUNT, 0) else 0
    }

    fun canSend(context: Context): Boolean {
        return getSendsToday(context) < FREE_DAILY_LIMIT
    }

    fun remainingSends(context: Context): Int {
        return (FREE_DAILY_LIMIT - getSendsToday(context)).coerceAtLeast(0)
    }

    fun recordSend(context: Context) {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val today = todayKey()
        val savedDate = prefs.getString(KEY_SEND_DATE, null)
        val currentCount = if (savedDate == today) prefs.getInt(KEY_SEND_COUNT, 0) else 0
        prefs.edit(commit = true) {
            putString(KEY_SEND_DATE, today)
            putInt(KEY_SEND_COUNT, currentCount + 1)
        }
    }
}
