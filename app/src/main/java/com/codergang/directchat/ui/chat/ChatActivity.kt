package com.codergang.directchat.ui.chat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import com.codergang.directchat.R
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.ActivityChatBinding
import com.codergang.directchat.ui.util.PhoneTextWatcher
import com.codergang.directchat.ui.util.hideKeyboard
import com.codergang.directchat.ui.util.setSafeOnClickListener
import com.codergang.directchat.ui.util.showSnackBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adView: AdView
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.bannerContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initBanner()
        setup()
        initObservers()
    }

    private fun setup() {
        val mensaje = intent.getStringExtra("message") ?: ""
        binding.etCarrierNumber.addTextChangedListener(PhoneTextWatcher())
        binding.btnChat.setSafeOnClickListener {
            val number = binding.etCarrierNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                openWhatsApp("${binding.ccp.selectedCountryCode}${number.replace("-", "")}", Uri.encode(mensaje))
            }else {
                Toast.makeText(this, getString(R.string.text_enter_phone_number) , Toast.LENGTH_LONG).show()
            }
        }
        setupPhone()
        binding.etContent.setText(mensaje)
    }

    private fun initObservers() {

    }

    private fun initBanner() {
        adView = AdView(this)
        adView.adUnitId = getString(R.string.banner_chat)
        binding.bannerContainer.addView(adView)
        adView.adSize = adSize
        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupPhone(){
        binding.phone.two.number.text = "2"
        binding.phone.three.number.text = "3"
        binding.phone.four.number.text = "4"
        binding.phone.five.number.text = "5"
        binding.phone.six.number.text = "6"
        binding.phone.seven.number.text = "7"
        binding.phone.eight.number.text = "8"
        binding.phone.nine.number.text = "9"
        binding.phone.zero.number.text = "0"
        binding.phone.asterisc.number.text = "*"
        binding.phone.michi.number.text = "#"
        binding.phone.asterisc.number.textSize = 30f

        binding.phone.one.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "1")
        }
        binding.phone.two.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "2")
        }
        binding.phone.three.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "3")
        }
        binding.phone.four.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "4")
        }
        binding.phone.five.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "5")
        }
        binding.phone.six.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "6")
        }
        binding.phone.seven.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "7")
        }
        binding.phone.eight.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "8")
        }
        binding.phone.nine.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "9")
        }
        binding.phone.zero.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "0")
        }
        binding.phone.asterisc.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "*")
        }
        binding.phone.michi.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "#")
        }
    }

    private fun openWhatsApp(numero: String, mensaje: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$numero/?text=$mensaje")
            startActivity(intent)
        } catch (e: Exception) {
            showSnackBar(binding.root, "WhatsApp is not Installed")
        }
    }

}