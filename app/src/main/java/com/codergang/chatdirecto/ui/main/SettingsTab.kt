package com.codergang.chatdirecto.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.vector.ImageVector
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
            .background(BrandSurface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

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
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        item { SectionHeader(text = stringResource(R.string.tex_support_us)) }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Outlined.Category,
                    text = stringResource(R.string.text_manage_categories),
                    onClick = onManageCategories
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Star,
                    text = stringResource(R.string.text_leave_a_review),
                    onClick = onRateApp
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Share,
                    text = stringResource(R.string.text_share_the_app),
                    onClick = onShareApp
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Apps,
                    text = stringResource(R.string.text_other_apps),
                    onClick = onOtherApps
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { SectionHeader(text = stringResource(R.string.text_about)) }
        item { BrandStrip() }
        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Outlined.Shield,
                    text = stringResource(R.string.text_privacy_policy),
                    onClick = onPrivacyPolicy
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.Description,
                    text = stringResource(R.string.text_terms_and_conditions),
                    onClick = onTerms
                )
            }
        }
        item {
            Text(
                text = stringResource(R.string.text_legal_disclaimer),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
                fontFamily = AvenirFamily,
                fontSize = 12.sp,
                color = ComposeColor(0xFF6B7280),
                lineHeight = 17.sp
            )
        }
        item {
            Text(
                text = "${stringResource(R.string.text_version)} ${BuildConfig.VERSION_NAME}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = ComposeColor(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
        fontFamily = AvenirFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = BrandPrimary,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ComposeColor.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BrandBorder),
        shadowElevation = 0.dp
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(BrandLavenderBg, shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = BrandText
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = ComposeColor(0xFFCBD5E1),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 64.dp),
        thickness = 1.dp,
        color = ComposeColor(0xFFF1F5F9)
    )
}

@Composable
private fun BrandStrip() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = BrandLavenderBg,
        shape = RoundedCornerShape(20.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.wordmark_lockup),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(vertical = 16.dp)
        )
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BrandLavenderBg,
        border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(BrandPrimary, shape = RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = ComposeColor.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.text_one_time_unlock),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = BrandText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when {
                            adsRemoved -> stringResource(R.string.text_ads_removed_active)
                            premiumUnlocked -> stringResource(R.string.text_premium_active)
                            else -> stringResource(R.string.text_remove_ads_forever)
                        },
                        fontFamily = AvenirFamily,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = ComposeColor(0xFF475569)
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

            Spacer(modifier = Modifier.height(16.dp))
            MonetizationBenefitRow(text = stringResource(R.string.text_pro_benefit_no_ads))
            MonetizationBenefitRow(text = stringResource(R.string.text_pro_benefit_power_tools))
            MonetizationBenefitRow(text = stringResource(R.string.text_pro_benefit_lifetime))

            if (!revenueCatReady) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = BrandAccent.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.text_purchases_unavailable),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        fontFamily = AvenirFamily,
                        fontSize = 13.sp,
                        color = BrandAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (!adsRemoved && revenueCatReady) {
                Button(
                    onClick = onOpenPaywall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = BrandPrimary,
                        contentColor = ComposeColor.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.text_buy_lifetime),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onRestorePurchases,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = stringResource(R.string.text_restore_purchase),
                        fontFamily = AvenirFamily,
                        color = BrandPrimary
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
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(BrandPrimary.copy(alpha = 0.14f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontFamily = AvenirFamily,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            color = BrandText
        )
    }
}

@Composable
private fun StatusBadge(text: String) {
    Surface(
        color = BrandPrimary.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = BrandPrimary
        )
    }
}
