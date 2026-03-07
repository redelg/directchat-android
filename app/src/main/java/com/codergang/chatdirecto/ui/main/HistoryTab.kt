package com.codergang.chatdirecto.ui.main

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codergang.chatdirecto.R
import com.codergang.chatdirecto.data.entity.ChatDB
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun HistoryTab(
    chats: List<ChatDB>,
    adsEnabled: Boolean,
    isPro: Boolean,
    onReuse: (ChatDB) -> Unit,
    onDelete: (ChatDB) -> Unit,
    onShare: (ChatDB) -> Unit,
    onUpgradeToPro: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(chats, query) {
        if (query.isBlank()) chats
        else chats.filter {
            it.formattedNumber.contains(query, true) ||
                it.number.contains(query, true) ||
                it.numberWithoutCode.contains(query, true)
        }
    }
    val totalCount = filtered.size
    val displayList = if (isPro || query.isNotBlank()) filtered else filtered.take(FREE_HISTORY_LIMIT)
    val isLimited = !isPro && query.isBlank() && totalCount > FREE_HISTORY_LIMIT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        ModernMaterialField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.text_enter_phone_number),
            singleLine = true,
            leadingIcon = Icons.Outlined.Search
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.ic_recall),
                    contentDescription = null,
                    tint = ComposeColor(0xFF777777),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.text_no_history), fontFamily = AvenirFamily)
                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(displayList, key = { it.id }) { item ->
                    HistoryItem(
                        item = item,
                        onReuse = { onReuse(item) },
                        onDelete = { onDelete(item) },
                        onShare = { onShare(item) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (isLimited) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.text_history_limit_free, FREE_HISTORY_LIMIT, totalCount),
                                fontFamily = AvenirFamily,
                                fontSize = 13.sp,
                                color = ComposeColor(0xFF777777)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = onUpgradeToPro) {
                                Text(
                                    text = stringResource(R.string.text_upgrade_to_pro),
                                    fontFamily = AvenirFamily,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }
            }
        }
        BannerAd(
            adUnitId = stringResource(R.string.banner_history),
            adsEnabled = adsEnabled
        )
    }
}

@Composable
private fun HistoryItem(
    item: ChatDB,
    onReuse: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReuse() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = ComposeColor.White),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_recall),
                contentDescription = null,
                tint = PrimaryGreenDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.formattedNumber, fontFamily = AvenirFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp)),
                    fontFamily = AvenirFamily,
                    fontSize = 12.sp,
                    color = ComposeColor(0xFF949494)
                )
            }
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Outlined.Share, contentDescription = null, tint = PrimaryGreen)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, tint = ComposeColor(0xFFD32F2F))
            }
        }
    }
}
