package com.codergang.directchat.ui.qr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import com.codergang.directchat.R
import com.codergang.directchat.databinding.ActivityCreateCodeBinding
import com.codergang.directchat.ui.util.setSafeOnClickListener
import com.github.sumimakito.awesomeqr.AwesomeQRCode
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
        val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.qr_center)

        Thread {
            kotlin.run {
                val qrBitmap = AwesomeQRCode.Renderer()
                    .contents(link)
                    .size(800)
                    .margin(20)
                    .dotScale(0.4f)
                    .colorDark(0xFF000000.toInt())
                    .colorLight(0xFFFFFFFF.toInt())
                    .logo(logoBitmap)
                    .logoMargin(10)
                    .logoRadius(10)
                    .logoScale(0.12f)
                    .render()

                runOnUiThread {
                    this.bitmap = qrBitmap
                    binding.qr.setImageBitmap(qrBitmap)
                }
            }
        }.start()
    }

    private fun shareImage(bitmap: Bitmap){
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val imagePath = File(cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri: Uri? =
            FileProvider.getUriForFile(this, "com.codergang.directchat.fileprovider", newFile)

        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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