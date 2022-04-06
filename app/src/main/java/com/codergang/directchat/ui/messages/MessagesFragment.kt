package com.codergang.directchat.ui.messages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.data.entity.MessageDB
import com.codergang.directchat.databinding.MessagesFragmentBinding
import com.codergang.directchat.ui.chat.ChatActivity
import com.codergang.directchat.ui.util.onChange

class MessagesFragment : Fragment() {

    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var binding: MessagesFragmentBinding
    private val adapter by lazy { MessageAdapter(::onClick, ::onShare, ::onDial) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = MessagesFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        initObservers()
    }

    private fun setup() {
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditMessageActivity::class.java))
        }
        binding.search.onChange {
            adapter.filter(it.toString())
        }
    }

    private fun initObservers() {
        viewModel.messages.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
            }
            adapter.items = it
            adapter.noFilterItems = it
        }
    }

    private fun onClick(item: MessageDB) {
        startActivity(Intent(requireContext(), AddEditMessageActivity::class.java).apply {
            putExtra("messageId", item.id)
        })
    }

    private fun onDial(item: MessageDB) {
        startActivity(Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("message", item.content)
        })
    }

    private fun onShare(item: MessageDB) {
        shareLink(item.content)
    }

    private fun shareLink(message: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
        i.putExtra(
            Intent.EXTRA_TEXT,
            message
        )
        startActivity(Intent.createChooser(i, "Share URL"))
    }

    private fun showEmpty() {
        binding.data.visibility = View.GONE
        binding.empty.visibility = View.VISIBLE
    }

    private fun hideEmpty() {
        binding.data.visibility = View.VISIBLE
        binding.empty.visibility = View.GONE
    }

}