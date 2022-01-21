package com.codergang.directchat.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.ui.chat.ChatFragment

class MainAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val items = mutableListOf<Fragment>()

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return items[position]
    }

    fun setItems(newItems: List<Fragment>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setNumber(item: ChatDB) {
        (items[0] as ChatFragment).setNumber(item)
    }
}