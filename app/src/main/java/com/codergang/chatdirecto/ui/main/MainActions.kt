package com.codergang.chatdirecto.ui.main

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.codergang.chatdirecto.BuildConfig
import com.codergang.chatdirecto.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal fun openChatLink(context: Context, number: String, message: String): Boolean {
    return try {
        val uri = Uri.parse("https://wa.me/$number/?text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }
}

internal fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Exception) {
        // no-op
    }
}

internal fun openStoreDetails(context: Context) {
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}"))
        )
    } catch (_: ActivityNotFoundException) {
        openUrl(context, "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
    }
}

internal fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.text_share_the_app)))
}

internal fun formatPhoneNumber(number: String): String {
    return number.chunked(3).joinToString(" ")
}

internal fun createQrBitmapWithLogo(context: Context, content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.qr_center)
        if (logo != null) {
            val canvas = Canvas(bitmap)
            val logoSize = (size * 0.16f).toInt()
            val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
            val left = (size - logoSize) / 2f
            val top = (size - logoSize) / 2f
            canvas.drawBitmap(scaledLogo, left, top, null)
        }

        bitmap
    } catch (_: Exception) {
        null
    }
}

internal fun buildWhatsAppLink(countryCode: String, phoneNumber: String, message: String): String {
    val encodedMessage = Uri.encode(message.trim())
    return "https://wa.me/$countryCode$phoneNumber/?text=$encodedMessage"
}

internal fun extractPhoneFromLink(link: String): String {
    val number = link.removePrefix("https://wa.me/")
        .substringBefore("/")
        .substringBefore("?")
    return "+$number"
}

internal fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("link", text))
}

internal fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
    return try {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Mensaji_QR_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Mensaji")
            }
        }
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        ) ?: return false
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        true
    } catch (_: Exception) {
        false
    }
}

internal fun shareBitmap(context: Context, bitmap: Bitmap, chooserTitle: String) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "share_qr.png")
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    } catch (_: IOException) {
        Toast.makeText(context, R.string.text_legal_unavailable, Toast.LENGTH_SHORT).show()
    }
}
