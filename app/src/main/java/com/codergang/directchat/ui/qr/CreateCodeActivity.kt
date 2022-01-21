package com.codergang.directchat.ui.qr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import com.codergang.directchat.R
import com.codergang.directchat.databinding.ActivityCreateCodeBinding
import com.codergang.directchat.ui.util.setSafeOnClickListener
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.android.gms.ads.AdRequest
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreateCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCodeBinding
    private var link = ""
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        link = intent.getStringExtra("link")!!

        generateQr()
        setup()
        loadAd()
    }

    private fun setup(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnShare.setSafeOnClickListener {
            bitmap?.let {
                shareImage(it)
            }
        }
        binding.btnNew.setSafeOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun generateQr(){
        val renderOption = RenderOption()
        renderOption.content = link // content to encode
        renderOption.size = 800 // size of the final QR code image
        renderOption.borderWidth = 20 // width of the empty space around the QR code
        renderOption.patternScale = 0.4f // (optional) specify a scale for patterns
        renderOption.color.dark = 0xFF000000.toInt()
        renderOption.color.background = 0x55000000.toInt()
        renderOption.roundedPatterns = false // (optional) if true, blocks will be drawn as dots instead
        renderOption.clearBorder = false // if set to true, the background will NOT be drawn on the border area
        renderOption.logo = Logo(
            BitmapFactory.decodeResource(resources,
                R.drawable.qr_center),
            0.12f,
            10,
            10,
            RectF(0f, 0f, 200f, 200f)
        )
        AwesomeQrRenderer.renderAsync(renderOption, { result ->
            if (result.bitmap != null) {
                this.bitmap = result.bitmap
                binding.qr.setImageBitmap(result.bitmap)
            }
        }, {
            exception -> exception.printStackTrace()
            // Oops, something gone wrong.
        })
    }

    private fun shareImage(bitmap: Bitmap){
        // save bitmap to cache directory
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //Share the image
        val imagePath = File(cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri: Uri? =
            FileProvider.getUriForFile(this, "com.codergang.directchat.fileprovider", newFile)

        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.string_share_image)))
        }
    }

    private fun loadAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
    }

}