package com.codergang.directchat.ui.util

import android.text.TextUtils

import android.text.method.DigitsKeyListener

import android.view.inputmethod.EditorInfo

import android.text.Editable

import android.R
import android.content.Context

import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


class PhoneNumberEditText : AppCompatEditText {
    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (isFormattedPhone(s.toString())) {
                return
            }
            val formatted = formatPhoneNumber(s.toString())
            setText(formatted)
            setSelection(formatted.length)
        }

        override fun afterTextChanged(s: Editable) {}
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(textWatcher)
        inputType = EditorInfo.TYPE_CLASS_PHONE
        keyListener = DigitsKeyListener.getInstance("0123456789")
        setSingleLine()
    }

    /**
     * @return
     */
    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        var result = true
        // Phone
        val regexPhone = "(\\+\\d{2,4})?\\s?(\\d{6,15})".toRegex()
        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches(regexPhone)) {
            result = false
        }
        return result
    }

    private fun isFormattedPhone(rawPhone: String): Boolean {
        val separate = rawPhone.split(" ".toRegex()).toTypedArray()
        if (separate.isEmpty()) {
            return true
        }
        if (separate.size == 1) {
            return separate[0].length <= 3
        }
        if (separate.size == 2) {
            return separate[0].length == 4 && separate[1].length <= 3
        }
        return if (separate.size >= 3) {
            separate[0].length == 4 && separate[1].length == 3
        } else true
    }

    /**
     * 0123456789xxx -> 0123 456 789xxx
     *
     * @param rawPhone
     * @return
     */
    private fun formatPhoneNumber(rawPhone: String): String {
        var rawPhone = rawPhone
        rawPhone = rawPhone.replace(" ".toRegex(), "")
        var phoneFormat = ""
        if (rawPhone.length > 4) {
            phoneFormat += rawPhone.substring(0, 4)
            rawPhone = rawPhone.substring(4)
        } else {
            return rawPhone
        }
        phoneFormat += " "
        if (rawPhone.length > 3) {
            phoneFormat += rawPhone.substring(0, 3)
            rawPhone = rawPhone.substring(3)
        } else {
            phoneFormat += rawPhone
            return phoneFormat
        }
        phoneFormat += " $rawPhone"
        return phoneFormat
    }

    val phoneNumber: String
        get() = text.toString().trim { it <= ' ' }.replace(" ".toRegex(), "")
}