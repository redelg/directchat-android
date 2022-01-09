package com.codergang.directchat.ui.util

import android.text.Editable
import android.text.TextWatcher

class PhoneTextWatcher: TextWatcher {
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
}