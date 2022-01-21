package com.codergang.directchat.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.codergang.directchat.R
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.ActivityMainBinding
import com.codergang.directchat.ui.chat.ChatFragment
import com.codergang.directchat.ui.history.HistoryFragment
import com.codergang.directchat.ui.link.LinkFragment
import com.google.android.material.tabs.TabLayoutMediator

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
            HistoryFragment(),
            LinkFragment(),
            Fragment()
        ))
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 4
        setTabLayout()
//        binding.bottomNav.setOnItemSelectedListener {
//            when(it.itemId){
//                R.id.menu_chat -> binding.viewPager.setCurrentItem(0, true)
//                R.id.menu_history -> binding.viewPager.setCurrentItem(1, true)
//            }
//            true
//        }
    }

    private fun setTabLayout(){
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when(pos){
                0 -> tab.setIcon(R.drawable.ic_phone)
                1 -> tab.setIcon(R.drawable.ic_history)
                2 -> tab.setIcon(R.drawable.ic_baseline_qr_code_24)
                3 -> tab.setIcon(R.drawable.ic_cog)
            }
        }.attach()
    }

    fun setHistoryNumber(item: ChatDB) {
        binding.viewPager.setCurrentItem(0, true)
        adapter.setNumber(item)
    }

}