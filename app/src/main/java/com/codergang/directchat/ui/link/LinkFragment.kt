package com.codergang.directchat.ui.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codergang.directchat.databinding.LinkFragmentBinding
import android.content.Intent
import com.codergang.directchat.ui.qr.CreateCodeActivity
import com.codergang.directchat.ui.util.setSafeOnClickListener


class LinkFragment : Fragment() {

    private val viewModel: LinkViewModel by viewModels()
    private lateinit var binding: LinkFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LinkFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup(){
        binding.btnLink.setSafeOnClickListener {
            shareLink()
        }
        binding.btnQr.setSafeOnClickListener {
            generateQr()
        }
    }

    private fun shareLink(){
        if(validate()){
            val number = binding.etCarrierNumber.text.toString().trim()
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
            i.putExtra(Intent.EXTRA_TEXT, "https://wa.me/${binding.ccp.selectedCountryCode}${number.replace("-", "")}")
            startActivity(Intent.createChooser(i, "Share URL"))
        }
    }

    private fun generateQr(){
        if(validate()){
            val number = binding.etCarrierNumber.text.toString().trim()
            startActivity(Intent(requireContext(), CreateCodeActivity::class.java).apply {
                putExtra("link", "https://wa.me/${binding.ccp.selectedCountryCode}${number.replace("-", "")}")
            })
        }
    }

    private fun validate(): Boolean{
        if(binding.etCarrierNumber.text.toString().isEmpty()){
           return false
        }
        return true
    }

}