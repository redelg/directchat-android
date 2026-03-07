package com.codergang.chatdirecto.ui.main

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codergang.chatdirecto.R
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.text.Normalizer
import java.util.Locale

internal data class CountryOption(
    val isoCode: String,
    val dialingCode: String,
    val localizedName: String,
    val englishName: String
) {
    val flagEmoji: String
        get() = countryFlagEmoji(isoCode)

    val compactLabel: String
        get() = "+$dialingCode"
}

@Composable
internal fun rememberCountryOptions(): List<CountryOption> {
    val context = LocalContext.current
    val locale = remember(context) {
        context.resources.configuration.locales[0] ?: Locale.getDefault()
    }
    return remember(locale) {
        CountryCatalog.load(locale)
    }
}

@Composable
internal fun rememberDefaultCountry(
    countries: List<CountryOption>
): CountryOption {
    val context = LocalContext.current
    return remember(countries, context) {
        CountryCatalog.detectDefault(context, countries)
    }
}

@Composable
internal fun CountryPickerField(
    selectedCountry: CountryOption,
    countries: List<CountryOption>,
    modifier: Modifier = Modifier,
    onCountrySelected: (CountryOption) -> Unit
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .clickable { showSheet = true },
        color = ComposeColor(0xFFF6FAF7),
        border = BorderStroke(1.dp, ComposeColor(0xFFE2EAE4)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = selectedCountry.flagEmoji,
                fontSize = 22.sp
            )
            Text(
                text = selectedCountry.compactLabel,
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ComposeColor(0xFF193B2A)
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = PrimaryGreenDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showSheet) {
        CountryPickerSheet(
            countries = countries,
            selectedCountry = selectedCountry,
            onDismiss = { showSheet = false },
            onCountrySelected = {
                onCountrySelected(it)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryPickerSheet(
    countries: List<CountryOption>,
    selectedCountry: CountryOption,
    onDismiss: () -> Unit,
    onCountrySelected: (CountryOption) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredCountries = remember(countries, query) {
        if (query.isBlank()) {
            countries
        } else {
            val normalizedQuery = normalizeSearch(query)
            countries.filter { country ->
                normalizeSearch(country.localizedName).contains(normalizedQuery) ||
                    normalizeSearch(country.englishName).contains(normalizedQuery) ||
                    country.isoCode.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                    country.dialingCode.contains(normalizedQuery)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ComposeColor.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.text_select_country),
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = ComposeColor(0xFF193B2A)
            )
            Spacer(modifier = Modifier.height(12.dp))
            ModernMaterialField(
                value = query,
                onValueChange = { query = it },
                label = stringResource(R.string.text_search_country),
                placeholder = stringResource(R.string.text_search_country_placeholder),
                leadingIcon = Icons.Outlined.Search
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCountries, key = { it.isoCode }) { country ->
                    CountryPickerRow(
                        country = country,
                        isSelected = country.isoCode == selectedCountry.isoCode,
                        onClick = { onCountrySelected(country) }
                    )
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun CountryPickerRow(
    country: CountryOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) {
                PrimaryGreen.copy(alpha = 0.10f)
            } else {
                ComposeColor.White
            }
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = country.flagEmoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.localizedName,
                    fontFamily = AvenirFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = ComposeColor(0xFF193B2A)
                )
                Text(
                    text = country.isoCode,
                    fontFamily = AvenirFamily,
                    fontSize = 12.sp,
                    color = ComposeColor(0xFF6A7E75)
                )
            }
            Text(
                text = country.compactLabel,
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isSelected) PrimaryGreenDark else ComposeColor(0xFF2D4A3C)
            )
        }
    }
}

private object CountryCatalog {
    private val phoneNumberUtil: PhoneNumberUtil by lazy { PhoneNumberUtil.getInstance() }

    fun load(locale: Locale): List<CountryOption> {
        return phoneNumberUtil.supportedRegions
            .mapNotNull { region ->
                val countryLocale = countryLocale(region)
                val localized = countryLocale.getDisplayCountry(locale).ifBlank {
                    countryLocale.getDisplayCountry(Locale.ENGLISH)
                }
                val english = countryLocale.getDisplayCountry(Locale.ENGLISH)
                val dialingCode = phoneNumberUtil.getCountryCodeForRegion(region)
                if (localized.isBlank() || dialingCode == 0) {
                    null
                } else {
                    CountryOption(
                        isoCode = region,
                        dialingCode = dialingCode.toString(),
                        localizedName = localized,
                        englishName = english
                    )
                }
            }
            .sortedWith(compareBy<CountryOption> { it.localizedName }.thenBy { it.isoCode })
    }

    fun detectDefault(
        context: Context,
        countries: List<CountryOption>
    ): CountryOption {
        val detectedIso = detectCountryIso(context)
        return countries.firstOrNull { it.isoCode.equals(detectedIso, ignoreCase = true) }
            ?: countries.firstOrNull { it.isoCode == "US" }
            ?: countries.first()
    }

    private fun detectCountryIso(context: Context): String {
        val telephonyManager = context.getSystemService(TelephonyManager::class.java)
        val networkCountry = telephonyManager?.networkCountryIso.orEmpty()
        if (networkCountry.isNotBlank()) return networkCountry.uppercase(Locale.ROOT)

        val simCountry = telephonyManager?.simCountryIso.orEmpty()
        if (simCountry.isNotBlank()) return simCountry.uppercase(Locale.ROOT)

        return context.resources.configuration.locales[0]?.country.orEmpty()
            .ifBlank { Locale.getDefault().country }
            .uppercase(Locale.ROOT)
    }
}

private fun normalizeSearch(value: String): String {
    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.ROOT)
        .trim()
}

private fun countryFlagEmoji(isoCode: String): String {
    if (isoCode.length != 2) return ""
    return isoCode.uppercase(Locale.ROOT)
        .map { character -> Character.toChars(character.code + 127397).concatToString() }
        .joinToString(separator = "")
}

private fun countryLocale(region: String): Locale {
    return Locale.Builder()
        .setRegion(region)
        .build()
}
