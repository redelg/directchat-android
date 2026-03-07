package com.codergang.chatdirecto.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ModernMaterialField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    leadingIcon: ImageVector? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    trailingContent: (@Composable (() -> Unit))? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        isError -> ComposeColor(0xFFD32F2F)
        isFocused -> PrimaryGreen
        else -> ComposeColor(0xFFE2EAE4)
    }
    val containerColor = when {
        isError -> ComposeColor(0xFFFFF6F6)
        isFocused -> PrimaryGreen.copy(alpha = 0.10f)
        else -> ComposeColor(0xFFF6FAF7)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isFocused) 4.dp else 0.dp
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = label,
                    fontFamily = AvenirFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            placeholder = if (placeholder != null) {
                {
                    Text(
                        text = placeholder,
                        fontFamily = AvenirFamily,
                        color = ComposeColor(0xFF98A5A0)
                    )
                }
            } else {
                null
            },
            supportingText = if (supportingText != null) {
                {
                    Text(
                        text = supportingText,
                        fontFamily = AvenirFamily,
                        fontSize = 12.sp,
                        color = if (isError) ComposeColor(0xFFD32F2F) else ComposeColor(0xFF6E7D76)
                    )
                }
            } else {
                null
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(imageVector = leadingIcon, contentDescription = null)
                }
            } else {
                null
            },
            trailingIcon = trailingContent,
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(20.dp),
            interactionSource = interactionSource,
            textStyle = TextStyle(
                color = ComposeColor(0xFF393939),
                fontFamily = AvenirFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ComposeColor.Transparent,
                unfocusedContainerColor = ComposeColor.Transparent,
                disabledContainerColor = ComposeColor.Transparent,
                errorContainerColor = ComposeColor.Transparent,
                focusedIndicatorColor = ComposeColor.Transparent,
                unfocusedIndicatorColor = ComposeColor.Transparent,
                disabledIndicatorColor = ComposeColor.Transparent,
                errorIndicatorColor = ComposeColor.Transparent,
                focusedLabelColor = PrimaryGreenDark,
                unfocusedLabelColor = ComposeColor(0xFF7A8B84),
                errorLabelColor = ComposeColor(0xFFD32F2F),
                focusedLeadingIconColor = PrimaryGreenDark,
                unfocusedLeadingIconColor = ComposeColor(0xFF7A8B84),
                errorLeadingIconColor = ComposeColor(0xFFD32F2F),
                focusedTrailingIconColor = PrimaryGreenDark,
                unfocusedTrailingIconColor = ComposeColor(0xFF7A8B84),
                errorTrailingIconColor = ComposeColor(0xFFD32F2F),
                cursorColor = PrimaryGreenDark,
                focusedTextColor = ComposeColor(0xFF393939),
                unfocusedTextColor = ComposeColor(0xFF393939),
                errorTextColor = ComposeColor(0xFF393939)
            )
        )
    }
}
