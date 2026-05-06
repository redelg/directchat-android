package com.codergang.chatdirecto.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codergang.chatdirecto.BuildConfig
import com.codergang.chatdirecto.R

@Composable
internal fun SettingsTab(
    adsRemoved: Boolean,
    premiumUnlocked: Boolean,
    showMonetization: Boolean,
    revenueCatReady: Boolean,
    onManageCategories: () -> Unit,
    onRateApp: () -> Unit,
    onShareApp: () -> Unit,
    onOtherApps: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    onRestorePurchases: () -> Unit,
    onOpenPaywall: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showMonetization) {
            item {
                MonetizationCard(
                    adsRemoved = adsRemoved,
                    premiumUnlocked = premiumUnlocked,
                    revenueCatReady = revenueCatReady,
                    onRestorePurchases = onRestorePurchases,
                    onOpenPaywall = onOpenPaywall
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.tex_support_us),
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Bold
            )
        }
        item { SettingsRow(text = stringResource(R.string.text_manage_categories), onClick = onManageCategories) }
        item { SettingsRow(text = stringResource(R.string.text_leave_a_review), onClick = onRateApp) }
        item { SettingsRow(text = stringResource(R.string.text_share_the_app), onClick = onShareApp) }
        item { SettingsRow(text = stringResource(R.string.text_other_apps), onClick = onOtherApps) }
        item { Spacer(modifier = Modifier.height(2.dp)) }
        item {
            Text(
                text = stringResource(R.string.text_about),
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Image(
                painter = painterResource(R.drawable.wordmark_lockup),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(vertical = 8.dp)
            )
        }
        item { SettingsRow(text = stringResource(R.string.text_privacy_policy), onClick = onPrivacyPolicy) }
        item { SettingsRow(text = stringResource(R.string.text_terms_and_conditions), onClick = onTerms) }
        item {
            Text(
                text = stringResource(R.string.text_legal_disclaimer),
                fontFamily = AvenirFamily,
                fontSize = 13.sp,
                color = ComposeColor(0xFF555555),
                lineHeight = 18.sp
            )
        }
        item {
            Text(
                text = "${stringResource(R.string.text_version)} ${BuildConfig.VERSION_NAME}",
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MonetizationCard(
    adsRemoved: Boolean,
    premiumUnlocked: Boolean,
    revenueCatReady: Boolean,
    onRestorePurchases: () -> Unit,
    onOpenPaywall: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = ComposeColor(0xFFF8FCF9)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    color = PrimaryGreen.copy(alpha = 0.12f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield_check),
                        contentDescription = null,
                        tint = PrimaryGreenDark,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.text_one_time_unlock),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = ComposeColor(0xFF18372B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            adsRemoved -> stringResource(R.string.text_ads_removed_active)
                            premiumUnlocked -> stringResource(R.string.text_premium_active)
                            else -> stringResource(R.string.text_remove_ads_forever)
                        },
                        fontFamily = AvenirFamily,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = ComposeColor(0xFF50635B)
                    )
                }
                StatusBadge(
                    text = if (adsRemoved) {
                        stringResource(R.string.text_status_active)
                    } else {
                        stringResource(R.string.text_status_lifetime)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            MonetizationBenefitRow(text = stringResource(R.string.text_pro_benefit_no_ads))
            MonetizationBenefitRow(text = stringResource(R.string.text_pro_benefit_power_tools))
            MonetizationBenefitRow(text = stringResource(R.string.text_pro_benefit_lifetime))

            if (!revenueCatReady) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = ComposeColor(0xFFFFF1F1),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.text_purchases_unavailable),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        fontFamily = AvenirFamily,
                        fontSize = 13.sp,
                        color = ComposeColor(0xFFB00020)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            if (!adsRemoved && revenueCatReady) {
                Button(
                    onClick = onOpenPaywall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.text_buy_lifetime),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onRestorePurchases,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = stringResource(R.string.text_restore_purchase),
                        fontFamily = AvenirFamily,
                        color = PrimaryGreenDark
                    )
                }
            }
        }
    }
}

@Composable
private fun MonetizationBenefitRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(26.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
            color = PrimaryGreen.copy(alpha = 0.14f)
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = PrimaryGreenDark,
                modifier = Modifier.padding(5.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontFamily = AvenirFamily,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            color = ComposeColor(0xFF31443C)
        )
    }
}

@Composable
private fun StatusBadge(text: String) {
    Surface(
        color = PrimaryGreen.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = PrimaryGreenDark
        )
    }
}

@Composable
private fun SettingsRow(
    text: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = ComposeColor.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.SemiBold
        )
    }
}
