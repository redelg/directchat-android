package com.codergang.directchat.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codergang.directchat.R
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.databinding.ChatFragmentBinding
import com.codergang.directchat.ui.util.PhoneTextWatcher
import com.codergang.directchat.ui.util.hideKeyboard
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
                        "${binding.ccp.selectedCountryCode}${number.replace("-", "")}",
                        "+${binding.ccp.selectedCountryCode} ${number.replace("-", " ")}",
                        number.replace("-", "")
                    )
                )
            }else {
                Toast.makeText(requireContext(), getString(R.string.text_enter_phone_number) , Toast.LENGTH_LONG).show()
            }
        }
        setupPhone()
    }

    private fun setupPhone(){
        binding.phone.two.number.text = "2"
        binding.phone.three.number.text = "3"
        binding.phone.four.number.text = "4"
        binding.phone.five.number.text = "5"
        binding.phone.six.number.text = "6"
        binding.phone.seven.number.text = "7"
        binding.phone.eight.number.text = "8"
        binding.phone.nine.number.text = "9"
        binding.phone.zero.number.text = "0"
        binding.phone.asterisc.number.text = "*"
        binding.phone.michi.number.text = "#"
        binding.phone.asterisc.number.textSize = 30f

        binding.phone.one.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "1")
        }
        binding.phone.two.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "2")
        }
        binding.phone.three.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "3")
        }
        binding.phone.four.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "4")
        }
        binding.phone.five.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "5")
        }
        binding.phone.six.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "6")
        }
        binding.phone.seven.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "7")
        }
        binding.phone.eight.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "8")
        }
        binding.phone.nine.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "9")
        }
        binding.phone.zero.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "0")
        }
        binding.phone.asterisc.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "*")
        }
        binding.phone.michi.root.setOnClickListener {
            hideKeyboard()
            binding.etCarrierNumber.clearFocus()
            binding.etCarrierNumber.setText(binding.etCarrierNumber.text.toString() + "#")
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

    fun setNumber(item: ChatDB){
        binding.etCarrierNumber.setText(item.numberWithoutCode)
    }

}