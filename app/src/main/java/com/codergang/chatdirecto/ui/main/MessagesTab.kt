package com.codergang.chatdirecto.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.codergang.chatdirecto.data.entity.CategoryDB
import com.codergang.chatdirecto.data.entity.MessageDB

@Composable
internal fun MessagesTab(
    messages: List<MessageDB>,
    categories: List<CategoryDB>,
    selectedCategoryId: Int?,
    isPro: Boolean,
    onCategorySelected: (Int?) -> Unit,
    onCreateMessage: () -> Unit,
    onEditMessage: (MessageDB) -> Unit,
    onDeleteMessage: (MessageDB) -> Unit,
    onShareMessage: (MessageDB) -> Unit,
    onUseInChat: (MessageDB) -> Unit,
    onToggleFavorite: (MessageDB) -> Unit,
    onLockedCategory: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }

    val categoryFiltered = remember(messages, selectedCategoryId) {
        if (selectedCategoryId == null) messages
        else messages.filter { it.categoryId == selectedCategoryId }
    }

    val filtered = remember(categoryFiltered, query) {
        if (query.isBlank()) categoryFiltered
        else categoryFiltered.filter {
            it.title.contains(query, true) || it.content.contains(query, true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            ModernMaterialField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.text_search_messages),
                singleLine = true,
                leadingIcon = Icons.Outlined.Search
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (categories.isNotEmpty()) {
                CategoryFilterBar(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    isPro = isPro,
                    onCategorySelected = onCategorySelected,
                    onLockedCategory = onLockedCategory
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(R.drawable.empty_no_templates),
                        contentDescription = null,
                        modifier = Modifier.size(220.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.text_no_predefined_messages),
                        fontFamily = AvenirFamily,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { message ->
                        MessageItem(
                            message = message,
                            category = categories.firstOrNull { it.id == message.categoryId },
                            onOpen = { onEditMessage(message) },
                            onDelete = { onDeleteMessage(message) },
                            onShare = { onShareMessage(message) },
                            onUse = { onUseInChat(message) },
                            onToggleFavorite = { onToggleFavorite(message) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateMessage,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PrimaryGreen,
            contentColor = ComposeColor.White
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_add_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CategoryFilterBar(
    categories: List<CategoryDB>,
    selectedCategoryId: Int?,
    isPro: Boolean,
    onCategorySelected: (Int?) -> Unit,
    onLockedCategory: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) },
                label = {
                    Text(
                        text = stringResource(R.string.text_all_categories),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor = ComposeColor.White
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
        items(categories, key = { it.id }) { category ->
            val isGeneral = category.id == 1
            val isLocked = !isPro && !isGeneral
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = {
                    if (isLocked) onLockedCategory()
                    else onCategorySelected(category.id)
                },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = localizedCategoryName(category),
                            fontFamily = AvenirFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        if (isLocked) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = ComposeColor(0xFF949494)
                            )
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor = ComposeColor.White
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
internal fun localizedCategoryName(category: CategoryDB): String {
    if (!category.isDefault) return category.name
    return when (category.name) {
        "General" -> stringResource(R.string.text_category_general)
        "Business" -> stringResource(R.string.text_category_business)
        "Personal" -> stringResource(R.string.text_category_personal)
        "Sales" -> stringResource(R.string.text_category_sales)
        "Support" -> stringResource(R.string.text_category_support)
        else -> category.name
    }
}

@Composable
private fun MessageItem(
    message: MessageDB,
    category: CategoryDB?,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onUse: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = ComposeColor.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = message.title, fontFamily = AvenirFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(
                    text = message.content,
                    fontFamily = AvenirFamily,
                    fontSize = 13.sp,
                    color = ComposeColor(0xFF949494),
                    maxLines = 3
                )
                if (category != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = localizedCategoryName(category),
                        fontFamily = AvenirFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreenDark
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (message.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (message.isFavorite) ComposeColor(0xFFE91E63) else ComposeColor(0xFF949494)
                )
            }
            IconButton(onClick = onUse) {
                Icon(painter = painterResource(R.drawable.ic_phone), contentDescription = null, tint = PrimaryGreen)
            }
            IconButton(onClick = onShare) {
                Icon(imageVector = Icons.Outlined.Share, contentDescription = null, tint = PrimaryGreenDark)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, tint = ComposeColor(0xFFD32F2F))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MessageEditorDialog(
    title: String,
    initialTitle: String,
    initialContent: String,
    categories: List<CategoryDB>,
    initialCategoryId: Int,
    isPro: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var localTitle by remember(initialTitle) { mutableStateOf(initialTitle) }
    var localContent by remember(initialContent) { mutableStateOf(initialContent) }
    var selectedCategoryId by remember(initialCategoryId) { mutableIntStateOf(initialCategoryId) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val availableCategories = remember(categories, isPro) {
        if (isPro) categories else categories.filter { it.id == 1 }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontFamily = AvenirFamily) },
        text = {
            Column {
                ModernMaterialField(
                    value = localTitle,
                    onValueChange = { localTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.text_enter_title),
                    singleLine = true,
                    leadingIcon = Icons.AutoMirrored.Outlined.Message
                )
                Spacer(modifier = Modifier.height(8.dp))
                ModernMaterialField(
                    value = localContent,
                    onValueChange = { localContent = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.text_enter_your_message),
                    minLines = 3,
                    singleLine = false,
                    leadingIcon = Icons.AutoMirrored.Outlined.Message
                )

                if (availableCategories.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        ModernMaterialField(
                            value = availableCategories.firstOrNull { it.id == selectedCategoryId }
                                ?.let { localizedCategoryName(it) } ?: "",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            label = stringResource(R.string.text_category),
                            trailingContent = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            availableCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = localizedCategoryName(cat),
                                            fontFamily = AvenirFamily
                                        )
                                    },
                                    onClick = {
                                        selectedCategoryId = cat.id
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(localTitle.trim(), localContent.trim(), selectedCategoryId) }) {
                Text(text = stringResource(R.string.text_save))
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text(text = stringResource(R.string.text_delete))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            }
        }
    )
}
