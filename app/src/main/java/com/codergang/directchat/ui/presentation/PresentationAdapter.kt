package com.codergang.directchat.ui.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PresentationAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
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

}