package com.codergang.directchat.ui.messages

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.data.entity.MessageDB
import com.codergang.directchat.databinding.ItemMensajeBinding
import com.codergang.directchat.ui.util.MessageDiffUtilCallback
import com.codergang.directchat.ui.util.setSafeOnClickListener
import kotlin.properties.Delegates

class MessageAdapter(
    val onClick: (item: MessageDB) -> Unit,
    val onShare: (item: MessageDB) -> Unit,
    val onDial: (item: MessageDB) -> Unit,
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var items: List<MessageDB> by Delegates.observable(emptyList()) { _, old, new ->
        DiffUtil.calculateDiff(MessageDiffUtilCallback(old, new)).dispatchUpdatesTo(this)
    }

    var noFilterItems = emptyList<MessageDB>()

    inner class ViewHolder(private val binding: ItemMensajeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageDB, position: Int) {
            binding.title.text = item.title
            binding.content.text = item.content
            binding.root.setSafeOnClickListener {
                onClick(item)
            }
            binding.share.setSafeOnClickListener {
                onShare(item)
            }
            binding.dial.setSafeOnClickListener {
                onDial(item)
            }
            if (position % 2 != 0) {
                binding.root.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                binding.root.setCardBackgroundColor(Color.parseColor("#F7FBFC"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMensajeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun filter(q: String) {
        items = if (q.isEmpty()) {
            noFilterItems
        } else {
            noFilterItems.filter {
                it.title.contains(q)
            }
        }
    }
}