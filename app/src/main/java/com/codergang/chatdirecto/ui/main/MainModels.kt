package com.codergang.chatdirecto.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.codergang.chatdirecto.R

internal const val FREE_TEMPLATE_LIMIT = 5
internal const val FREE_HISTORY_LIMIT = 3

internal enum class MainTab(
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int
) {
    Chat(R.drawable.ic_phone, R.string.nav_chat),
    History(R.drawable.ic_history, R.string.nav_history),
    Messages(R.drawable.ic_baseline_message_24, R.string.nav_templates),
    Settings(R.drawable.ic_cog, R.string.nav_settings)
}
