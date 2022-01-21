package com.codergang.directchat.ui.history

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codergang.directchat.R
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.HistoryFragmentBinding
import com.codergang.directchat.ui.main.MainActivity
import com.codergang.directchat.ui.util.onChange
import com.google.android.gms.ads.AdRequest

class HistoryFragment : Fragment() {

    private lateinit var binding: HistoryFragmentBinding
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter by lazy { HistoryAdapter(this::onClick, this::onDeleteItem, this::onShareItem) }
    private val activity by lazy { requireActivity() as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HistoryFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        setObservers()
        loadAd()
    }

    private fun setup() {
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.search.onChange {
            adapter.filter(it.toString())
        }
    }

    private fun setObservers() {
        viewModel.chats.observe(viewLifecycleOwner, {
            if(it.isEmpty()){
                showEmpty()
            }else {
                hideEmpty()
            }
            adapter.items = it
            adapter.noFilterItems = it
        })
    }

    private fun onClick(item: ChatDB) {
        activity.setHistoryNumber(item)
    }

    private fun onDeleteItem(item: ChatDB) {
        viewModel.deleteItem(item)
    }

    private fun onShareItem(item: ChatDB) {
        shareLink(item)
    }

    private fun shareLink(item: ChatDB) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
        i.putExtra(
            Intent.EXTRA_TEXT,
            "https://wa.me/${item.number}"
        )
        startActivity(Intent.createChooser(i, "Share URL"))
    }

    private fun loadAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
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