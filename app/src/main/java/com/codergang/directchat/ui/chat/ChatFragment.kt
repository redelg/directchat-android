package com.codergang.directchat.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.ChatFragmentBinding
import com.codergang.directchat.ui.util.PhoneTextWatcher
import com.codergang.directchat.ui.util.setSafeOnClickListener
import com.codergang.directchat.ui.util.showSnackBar
import com.google.android.gms.ads.AdRequest
import java.util.*


class ChatFragment : Fragment() {

    private lateinit var binding: ChatFragmentBinding
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        loadAd()
    }

    private fun loadAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
    }

    private fun setup() {
        binding.etCarrierNumber.addTextChangedListener(PhoneTextWatcher())
        binding.btnChat.setSafeOnClickListener {
            val number = binding.etCarrierNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                openWhatsApp("${binding.ccp.selectedCountryCode}${number.replace("-", "")}")
                viewModel.saveChat(
                    ChatDB(
                        Date().time,
                        number
                    )
                )
            }
        }
    }

    private fun openWhatsApp(numero: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$numero/?text=")
            startActivity(intent)
        } catch (e: Exception) {
            showSnackBar(binding.root, "Para continuar, instale Whatsapp")
        }
//        return
//        val sendIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$numero"))
//        sendIntent.setPackage("com.whatsapp")
//        if (requireActivity().intent.resolveActivity(requireActivity().packageManager) == null) {
//            showSnackBar(binding.root, "Para continuar, instale Whatsapp")
//            return
//        }
//        startActivity(sendIntent)
    }


}