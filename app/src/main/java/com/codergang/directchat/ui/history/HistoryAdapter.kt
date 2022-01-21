package com.codergang.directchat.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.ItemHistoryBinding
import com.codergang.directchat.ui.util.ChatDiffUtilCallback
import com.codergang.directchat.ui.util.localizedString
import com.codergang.directchat.ui.util.setSafeOnClickListener
import java.util.*
import kotlin.properties.Delegates

class HistoryAdapter(
    val onClick: (ChatDB) -> Unit,
    val onDelete: (ChatDB) -> Unit,
    val onShare: (ChatDB) -> Unit
): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var items: List<ChatDB> by Delegates.observable(emptyList()) { _, old, new ->
        DiffUtil.calculateDiff(ChatDiffUtilCallback(old, new)).dispatchUpdatesTo(this)
    }

    var noFilterItems = emptyList<ChatDB>()

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatDB, position: Int){
            binding.number.text = item.formattedNumber
            binding.date.text = Date(item.timestamp).localizedString()
            binding.root.setSafeOnClickListener {
                onClick.invoke(item)
            }
            binding.delete.setSafeOnClickListener {
                onDelete.invoke(item)
            }
            binding.share.setSafeOnClickListener {
                onShare.invoke(item)
            }
            if(position % 2 != 0){
                binding.root.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }else {
                binding.root.setCardBackgroundColor(Color.parseColor("#F7FBFC"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
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