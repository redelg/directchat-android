package com.codergang.directchat.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.codergang.directchat.R
import com.codergang.directchat.databinding.ActivityMainBinding
import com.codergang.directchat.ui.chat.ChatFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { MainAdapter(supportFragmentManager, lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup(){
        adapter.setItems(listOf(
            ChatFragment(),
            ChatFragment()
        ))
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 2
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_chat -> binding.viewPager.setCurrentItem(0, true)
                R.id.menu_history -> binding.viewPager.setCurrentItem(1, true)
            }
            true
        }
    }
}