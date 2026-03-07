package com.codergang.chatdirecto.ui.main

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.codergang.chatdirecto.R
import com.codergang.chatdirecto.data.entity.CategoryDB
import kotlinx.coroutines.delay

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LegalScreen(
    title: String,
    assetFileName: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, fontFamily = AvenirFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AndroidView(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            factory = { context ->
                android.webkit.WebView(context).apply {
                    settings.javaScriptEnabled = false
                    loadUrl("file:///android_asset/$assetFileName")
                }
            }
        )
    }
}

@Composable
internal fun QrSheetContent(
    phoneCode: String,
    phoneNumber: String,
    initialMessage: String,
    adsEnabled: Boolean
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val countries = rememberCountryOptions()
    val defaultCountry = rememberDefaultCountry(countries)

    var selectedCountryIso by rememberSaveable {
        mutableStateOf(
            if (phoneCode.isNotBlank()) {
                countries.firstOrNull { it.dialingCode == phoneCode }?.isoCode
                    ?: defaultCountry.isoCode
            } else defaultCountry.isoCode
        )
    }
    val selectedCountry = remember(selectedCountryIso, countries, defaultCountry) {
        countries.firstOrNull { it.isoCode == selectedCountryIso } ?: defaultCountry
    }
    var numberInput by rememberSaveable { mutableStateOf(phoneNumber) }
    var messageInput by rememberSaveable { mutableStateOf(initialMessage) }

    val cleanNumber = remember(numberInput) { numberInput.filter { it.isDigit() } }
    val hasNumber = cleanNumber.isNotBlank()
    val link = if (hasNumber) buildWhatsAppLink(selectedCountry.dialingCode, cleanNumber, messageInput) else ""

    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = link) {
        value = if (hasNumber) createQrBitmapWithLogo(context, link, 900) else null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = screenHeight * 0.85f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.text_qr_code),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Phone input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CountryPickerField(
                selectedCountry = selectedCountry,
                countries = countries,
                onCountrySelected = { selectedCountryIso = it.isoCode }
            )
            Spacer(modifier = Modifier.width(10.dp))
            ModernMaterialField(
                value = numberInput,
                onValueChange = { numberInput = it.filter { c -> c.isDigit() } },
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.text_enter_phone_number),
                placeholder = stringResource(R.string.text_enter_phone_number),
                keyboardType = KeyboardType.Phone
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optional message
        ModernMaterialField(
            value = messageInput,
            onValueChange = { messageInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.text_optional_message),
            placeholder = stringResource(R.string.text_message_optional),
            supportingText = stringResource(R.string.text_message_optional_hint),
            keyboardType = KeyboardType.Text,
            singleLine = false,
            minLines = 2
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!hasNumber) {
            Text(
                text = stringResource(R.string.text_enter_number_first),
                fontFamily = AvenirFamily,
                fontSize = 15.sp,
                color = ComposeColor(0xFF777777),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )
        } else {
            if (bitmap == null) {
                CircularProgressIndicator(color = PrimaryGreen)
            } else {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(220.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (bitmap != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val saved = saveBitmapToGallery(context, bitmap!!)
                            Toast.makeText(
                                context,
                                if (saved) R.string.text_photo_saved else R.string.text_legal_unavailable,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(
                            text = stringResource(R.string.text_save_photo),
                            fontFamily = AvenirFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            shareBitmap(
                                context,
                                bitmap!!,
                                context.getString(R.string.string_share_image)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, PrimaryGreen)
                    ) {
                        Text(
                            text = stringResource(R.string.text_share),
                            fontFamily = AvenirFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BannerAd(
                adUnitId = stringResource(R.string.banner_qr),
                adsEnabled = adsEnabled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
internal fun LinkSheetContent(
    phoneCode: String,
    phoneNumber: String,
    initialMessage: String,
    adsEnabled: Boolean
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val countries = rememberCountryOptions()
    val defaultCountry = rememberDefaultCountry(countries)

    var selectedCountryIso by rememberSaveable {
        mutableStateOf(
            if (phoneCode.isNotBlank()) {
                countries.firstOrNull { it.dialingCode == phoneCode }?.isoCode
                    ?: defaultCountry.isoCode
            } else defaultCountry.isoCode
        )
    }
    val selectedCountry = remember(selectedCountryIso, countries, defaultCountry) {
        countries.firstOrNull { it.isoCode == selectedCountryIso } ?: defaultCountry
    }
    var numberInput by rememberSaveable { mutableStateOf(phoneNumber) }
    var messageInput by rememberSaveable { mutableStateOf(initialMessage) }
    var copied by remember { mutableStateOf(false) }

    val cleanNumber = remember(numberInput) { numberInput.filter { it.isDigit() } }
    val hasNumber = cleanNumber.isNotBlank()
    val link = if (hasNumber) buildWhatsAppLink(selectedCountry.dialingCode, cleanNumber, messageInput) else ""

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = screenHeight * 0.85f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.text_link_generator),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Phone input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CountryPickerField(
                selectedCountry = selectedCountry,
                countries = countries,
                onCountrySelected = { selectedCountryIso = it.isoCode }
            )
            Spacer(modifier = Modifier.width(10.dp))
            ModernMaterialField(
                value = numberInput,
                onValueChange = { numberInput = it.filter { c -> c.isDigit() } },
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.text_enter_phone_number),
                placeholder = stringResource(R.string.text_enter_phone_number),
                keyboardType = KeyboardType.Phone
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optional message
        ModernMaterialField(
            value = messageInput,
            onValueChange = { messageInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.text_optional_message),
            placeholder = stringResource(R.string.text_message_optional),
            supportingText = stringResource(R.string.text_message_optional_hint),
            keyboardType = KeyboardType.Text,
            singleLine = false,
            minLines = 2
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!hasNumber) {
            Text(
                text = stringResource(R.string.text_enter_number_first),
                fontFamily = AvenirFamily,
                fontSize = 15.sp,
                color = ComposeColor(0xFF777777),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )
        } else {
            // Generated link
            Text(
                text = stringResource(R.string.text_generated_link),
                fontFamily = AvenirFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ComposeColor(0xFF777777),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            SelectionContainer {
                Text(
                    text = link,
                    fontFamily = AvenirFamily,
                    fontSize = 14.sp,
                    color = PrimaryGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            PrimaryGreenLight.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        copyToClipboard(context, link)
                        copied = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(
                        text = stringResource(
                            if (copied) R.string.text_copied else R.string.text_copy_link
                        ),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { shareText(context, link) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, PrimaryGreen)
                ) {
                    Text(
                        text = stringResource(R.string.text_share),
                        fontFamily = AvenirFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BannerAd(
                adUnitId = stringResource(R.string.banner_link),
                adsEnabled = adsEnabled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
internal fun CategoryManagementSheetContent(
    categories: List<CategoryDB>,
    onAddCategory: (String) -> Unit,
    onRenameCategory: (CategoryDB, String) -> Unit,
    onDeleteCategory: (CategoryDB) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryDB?>(null) }
    var deletingCategory by remember { mutableStateOf<CategoryDB?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = screenHeight * 0.6f)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.text_manage_categories),
            fontFamily = AvenirFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            items(categories, key = { it.id }) { category ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = ComposeColor.White),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = localizedCategoryName(category),
                                fontFamily = AvenirFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            if (category.isDefault) {
                                Text(
                                    text = "Default",
                                    fontFamily = AvenirFamily,
                                    fontSize = 12.sp,
                                    color = ComposeColor(0xFF949494)
                                )
                            }
                        }
                        if (!category.isDefault) {
                            IconButton(onClick = { editingCategory = category }) {
                                Icon(
                                    imageVector = Outlined.Edit,
                                    contentDescription = stringResource(R.string.text_rename_category),
                                    tint = PrimaryGreenDark
                                )
                            }
                            IconButton(onClick = { deletingCategory = category }) {
                                Icon(
                                    imageVector = Outlined.Delete,
                                    contentDescription = stringResource(R.string.text_delete_category),
                                    tint = ComposeColor(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(
                imageVector = Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.text_add_category),
                fontFamily = AvenirFamily,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showAddDialog) {
        CategoryNameDialog(
            title = stringResource(R.string.text_add_category),
            initialName = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                onAddCategory(name)
                showAddDialog = false
            }
        )
    }

    if (editingCategory != null) {
        val cat = editingCategory!!
        CategoryNameDialog(
            title = stringResource(R.string.text_rename_category),
            initialName = cat.name,
            onDismiss = { editingCategory = null },
            onConfirm = { name ->
                onRenameCategory(cat, name)
                editingCategory = null
            }
        )
    }

    if (deletingCategory != null) {
        val cat = deletingCategory!!
        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = { Text(text = stringResource(R.string.text_delete_category), fontFamily = AvenirFamily) },
            text = { Text(text = stringResource(R.string.text_delete_category_confirm), fontFamily = AvenirFamily) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCategory(cat)
                    onShowMessage(context.getString(R.string.text_messages_moved_to_general))
                    deletingCategory = null
                }) {
                    Text(text = stringResource(R.string.text_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun CategoryNameDialog(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontFamily = AvenirFamily) },
        text = {
            ModernMaterialField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.text_category_name),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isNotBlank()) onConfirm(trimmed)
                }
            ) {
                Text(text = stringResource(R.string.text_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        }
    )
}
