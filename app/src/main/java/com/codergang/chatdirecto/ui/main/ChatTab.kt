package com.codergang.chatdirecto.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codergang.chatdirecto.R
import com.codergang.chatdirecto.data.entity.ChatDB
import com.codergang.chatdirecto.data.entity.MessageDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class PrimaryActionState {
    Idle,
    Loading,
    Success
}

@Composable
internal fun ChatTab(
    adsEnabled: Boolean,
    isPro: Boolean,
    recentChats: List<ChatDB>,
    favoriteMessages: List<MessageDB>,
    pendingNumber: String?,
    pendingMessage: String?,
    onConsumePendingNumber: () -> Unit,
    onConsumePendingMessage: () -> Unit,
    onShowMessage: (String) -> Unit,
    onOpenChat: (countryCode: String, number: String, message: String) -> Unit,
    onCurrentInputChanged: (phoneCode: String, phoneNumber: String, message: String) -> Unit = { _, _, _ -> }
) {
    val enterPhoneText = stringResource(R.string.text_enter_phone_number)
    val enterMessageText = stringResource(R.string.text_enter_your_message)
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val countries = rememberCountryOptions()
    val defaultCountry = rememberDefaultCountry(countries)
    var selectedCountryIso by rememberSaveable { mutableStateOf(defaultCountry.isoCode) }
    val selectedCountry = remember(selectedCountryIso, countries, defaultCountry) {
        countries.firstOrNull { it.isoCode == selectedCountryIso } ?: defaultCountry
    }
    var numberInput by rememberSaveable { mutableStateOf("") }
    var messageInput by rememberSaveable { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var actionState by remember { mutableStateOf(PrimaryActionState.Idle) }

    val cleanNumber = remember(numberInput) {
        numberInput.filter { it.isDigit() }
    }
    val previewText = remember(selectedCountry.isoCode, selectedCountry.dialingCode, cleanNumber) {
        if (cleanNumber.isBlank()) null else "+${selectedCountry.dialingCode} ${formatPhoneNumber(cleanNumber)}"
    }

    val actionButtonColor by animateColorAsState(
        targetValue = when (actionState) {
            PrimaryActionState.Idle -> PrimaryGreen
            PrimaryActionState.Loading -> PrimaryGreenDark
            PrimaryActionState.Success -> PrimaryGreenLight
        },
        label = "chatActionColor"
    )

    // Deduplicated recent contacts
    val displayedChats = remember(recentChats, isPro) {
        val unique = recentChats.distinctBy { it.number }
        if (isPro) unique else unique.take(FREE_HISTORY_LIMIT)
    }

    val displayedFavorites = remember(favoriteMessages) {
        favoriteMessages.filter { it.isFavorite }
    }

    LaunchedEffect(defaultCountry.isoCode) {
        if (selectedCountryIso.isBlank()) {
            selectedCountryIso = defaultCountry.isoCode
        }
    }
    LaunchedEffect(pendingNumber) {
        pendingNumber?.let {
            numberInput = it.filter { character -> character.isDigit() }
            phoneError = null
            onConsumePendingNumber()
        }
    }
    LaunchedEffect(pendingMessage) {
        pendingMessage?.let {
            messageInput = it
            onConsumePendingMessage()
        }
    }
    LaunchedEffect(cleanNumber, selectedCountry.dialingCode, messageInput) {
        onCurrentInputChanged(selectedCountry.dialingCode, cleanNumber, messageInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Recent Contacts Bar
        if (displayedChats.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(displayedChats, key = { it.id }) { chat ->
                    RecentContactChip(
                        text = chat.formattedNumber,
                        onClick = {
                            numberInput = chat.numberWithoutCode
                            phoneError = null
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Favorites Bar
        if (displayedFavorites.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(displayedFavorites, key = { it.id }) { message ->
                    FavoriteMessageChip(
                        text = message.title,
                        onClick = { messageInput = message.content }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = ComposeColor(0xFFF8FCF9)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountryPickerField(
                        selectedCountry = selectedCountry,
                        countries = countries,
                        onCountrySelected = {
                            selectedCountryIso = it.isoCode
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ModernMaterialField(
                        value = numberInput,
                        onValueChange = {
                            numberInput = it.filter { character -> character.isDigit() }
                            phoneError = null
                        },
                        modifier = Modifier.weight(1f),
                        label = enterPhoneText,
                        placeholder = enterPhoneText,
                        keyboardType = KeyboardType.Phone,
                        isError = phoneError != null
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(visible = phoneError != null || previewText != null) {
                        InlineStatusPill(
                            text = phoneError ?: stringResource(
                                R.string.text_number_preview,
                                previewText.orEmpty()
                            ),
                            isError = phoneError != null
                        )
                    }
                }
                ModernMaterialField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(146.dp),
                    label = enterMessageText,
                    placeholder = stringResource(R.string.text_message_optional),
                    supportingText = stringResource(R.string.text_message_optional_hint),
                    keyboardType = KeyboardType.Text,
                    minLines = 4,
                    singleLine = false
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                if (cleanNumber.isBlank()) {
                    phoneError = enterPhoneText
                    onShowMessage(enterPhoneText)
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    return@Button
                }
                phoneError = null
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                scope.launch {
                    actionState = PrimaryActionState.Loading
                    delay(220)
                    onOpenChat(selectedCountry.dialingCode, cleanNumber, messageInput.trim())
                    actionState = PrimaryActionState.Success
                    delay(420)
                    actionState = PrimaryActionState.Idle
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor)
        ) {
            if (actionState == PrimaryActionState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = ComposeColor.White
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (actionState == PrimaryActionState.Success) {
                            stringResource(R.string.text_done)
                        } else {
                            stringResource(R.string.text_chat)
                        },
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        BannerAd(
            adUnitId = stringResource(R.string.banner_chat),
            adsEnabled = adsEnabled
        )
    }
}

@Composable
private fun RecentContactChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = PrimaryGreen,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = ComposeColor.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FavoriteMessageChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = PrimaryGreen.copy(alpha = 0.12f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "\u2B50", fontSize = 12.sp)
            Text(
                text = text,
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = PrimaryGreenDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InlineStatusPill(
    text: String,
    isError: Boolean
) {
    val background = if (isError) ComposeColor(0xFFFFE9E9) else PrimaryGreen.copy(alpha = 0.12f)
    val contentColor = if (isError) ComposeColor(0xFFD32F2F) else PrimaryGreenDark
    Surface(
        color = background,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontFamily = AvenirFamily,
            fontSize = 12.sp,
            color = contentColor
        )
    }
}
