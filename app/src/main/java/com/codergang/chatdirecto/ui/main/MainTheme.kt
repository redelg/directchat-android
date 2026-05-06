package com.codergang.chatdirecto.ui.main

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.codergang.chatdirecto.R

// Mensaji brand palette
internal val BrandPrimary = ComposeColor(0xFF6C5CE7)
internal val BrandPrimaryDark = ComposeColor(0xFF4834D4)
internal val BrandAccent = ComposeColor(0xFFFF6B68)
internal val BrandSurface = ComposeColor(0xFFF8FAFC)
internal val BrandLavenderBg = ComposeColor(0xFFF2F0FF)
internal val BrandText = ComposeColor(0xFF0F172A)
internal val BrandBorder = ComposeColor(0xFFE2E8F0)

// Legacy aliases — kept so existing call sites compile until they're migrated
internal val PrimaryGreen = BrandPrimary
internal val PrimaryGreenDark = BrandPrimaryDark
internal val PrimaryGreenLight = BrandAccent

internal val AvenirFamily = FontFamily(
    Font(R.font.avenir, FontWeight.Normal),
    Font(R.font.avenir_demi, FontWeight.SemiBold),
    Font(R.font.avenir_bold, FontWeight.Bold)
)

@Composable
internal fun DirectChatTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = BrandPrimary,
        secondary = BrandPrimaryDark,
        tertiary = BrandAccent,
        background = BrandSurface,
        surface = ComposeColor.White,
        onPrimary = ComposeColor.White,
        onSecondary = ComposeColor.White,
        onBackground = BrandText,
        onSurface = BrandText,
        outline = BrandBorder
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
