package com.codergang.directchat.ui.history

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
import com.codergang.directchat.databinding.HistoryFragmentBinding
import com.codergang.directchat.ui.util.onChange

class HistoryFragment : Fragment() {

    private lateinit var binding: HistoryFragmentBinding
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter by lazy { HistoryAdapter() }

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
    }

    private fun setup() {
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        binding.search.onChange {
            adapter.filter(it.toString())
        }
    }

    private fun setObservers(){
        viewModel.chats.observe(viewLifecycleOwner, {
            adapter.items = it
            adapter.noFilterItems = it
        })
    }

}