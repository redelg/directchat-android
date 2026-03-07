package com.codergang.chatdirecto.ui.main

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.codergang.chatdirecto.R

internal val PrimaryGreen = ComposeColor(0xFF29AA6F)
internal val PrimaryGreenDark = ComposeColor(0xFF008580)
internal val PrimaryGreenLight = ComposeColor(0xFF46F696)

internal val AvenirFamily = FontFamily(
    Font(R.font.avenir, FontWeight.Normal),
    Font(R.font.avenir_demi, FontWeight.SemiBold),
    Font(R.font.avenir_bold, FontWeight.Bold)
)

@Composable
internal fun DirectChatTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryGreen,
        secondary = PrimaryGreenDark,
        tertiary = PrimaryGreenLight,
        background = ComposeColor.White,
        surface = ComposeColor.White,
        onPrimary = ComposeColor.White,
        onSecondary = ComposeColor.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography.copy(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = AvenirFamily),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = AvenirFamily),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = AvenirFamily),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = AvenirFamily)
        ),
        content = content
    )
}
