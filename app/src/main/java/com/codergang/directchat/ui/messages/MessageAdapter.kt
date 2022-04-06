package com.codergang.directchat.ui.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codergang.directchat.data.entity.MessageDB
import com.codergang.directchat.databinding.ItemMensajeBinding
import com.codergang.directchat.ui.util.setSafeOnClickListener

class MessageAdapter(
    val onClick: (item: MessageDB) -> Unit,
    val onShare: (item: MessageDB) -> Unit,
    val onDial: (item: MessageDB) -> Unit
): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val items = mutableListOf<MessageDB>()

    inner class ViewHolder(private val binding: ItemMensajeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MessageDB) {
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMensajeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<MessageDB>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}