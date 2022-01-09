package com.codergang.directchat.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.ItemHistoryBinding
import com.codergang.directchat.ui.util.ChatDiffUtilCallback
import kotlin.properties.Delegates

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var items: List<ChatDB> by Delegates.observable(emptyList()) { _, old, new ->
        DiffUtil.calculateDiff(ChatDiffUtilCallback(old, new)).dispatchUpdatesTo(this)
    }

    var noFilterItems = emptyList<ChatDB>()

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatDB){
            binding.number.text = item.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun filter(q: String) {
        items = if(q.isEmpty()){
            noFilterItems
        }else {
            noFilterItems.filter {
                it.number.contains(q)
            }
        }
    }
}