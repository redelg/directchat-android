package com.codergang.directchat.ui.util

import androidx.recyclerview.widget.DiffUtil
import com.codergang.directchat.data.entity.ChatDB

class ChatDiffUtilCallback(private val oldList: List<ChatDB>, private val newList: List<ChatDB>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]
}