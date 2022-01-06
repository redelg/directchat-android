package com.codergang.directchat.ui.chat

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codergang.directchat.R
import com.codergang.directchat.databinding.ChatFragmentBinding
import com.codergang.directchat.ui.util.setSafeOnClickListener
import com.codergang.directchat.ui.util.showSnackBar
import java.lang.Exception
import java.net.URLEncoder
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.doAfterTextChanged


class ChatFragment : Fragment() {

    private lateinit var binding: ChatFragmentBinding

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
    }

    private fun setup(){
        binding.etCarrierNumber.addTextChangedListener(object : TextWatcher {
            var length_before = 0

            override fun beforeTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                length_before = s.length
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (length_before < s.length) {
                    if (s.length == 3 || s.length == 7)
                        s.append("-");
                    if (s.length > 3) {
                        if (Character.isDigit(s[3]))
                            s.insert(3, "-");
                    }
                    if (s.length > 7) {
                        if (Character.isDigit(s[7]))
                            s.insert(7, "-");
                    }
                }
            }

        })
        binding.btnChat.setSafeOnClickListener {
            val number = binding.etCarrierNumber.text.toString().trim()
            if(number.isNotEmpty()){
                openWhatsApap("${binding.ccp.selectedCountryCode}${number.replace("-", "")}")
            }
        }
    }

    private fun openWhatsApap(numero: String) {
        val sendIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$numero"))
        sendIntent.setPackage("com.whatsapp")
        if (requireActivity().intent.resolveActivity(requireActivity().packageManager) == null) {
            showSnackBar(binding.root, "Para continuar, instale Whatsapp")
            return
        }
        startActivity(sendIntent)
    }


}