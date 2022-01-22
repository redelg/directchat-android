package com.codergang.directchat.ui.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codergang.directchat.R
import com.codergang.directchat.data.preferences.UserPreferences
import com.codergang.directchat.data.preferences.Usuario
import com.codergang.directchat.databinding.ActivityPresentationBinding
import com.codergang.directchat.ui.chat.ChatFragment
import com.codergang.directchat.ui.history.HistoryFragment
import com.codergang.directchat.ui.link.LinkFragment
import com.codergang.directchat.ui.main.MainActivity
import com.codergang.directchat.ui.main.MainAdapter
import com.codergang.directchat.ui.settings.SettingsFragment
import com.codergang.directchat.ui.util.setSafeOnClickListener

class PresentationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresentationBinding
    private val adapter by lazy { PresentationAdapter(supportFragmentManager, lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresentationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup() {
        adapter.setItems(listOf(
            StepOneFragment(),
            StepTwoFragment(),
            StepThreeFragment(),
        ))
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 3
        binding.wormDotsIndicator.setViewPager2(binding.viewPager)

        binding.btnNext.setSafeOnClickListener {
            moveNext()
        }
        binding.cvSkip.setSafeOnClickListener {
            goToMain()
        }
    }

    private fun moveNext(){
        when(binding.viewPager.currentItem){
            2 -> {
                goToMain()
            }
            else -> {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }
        }
    }

    private fun goToMain() {
        UserPreferences.set(this, Usuario())
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}