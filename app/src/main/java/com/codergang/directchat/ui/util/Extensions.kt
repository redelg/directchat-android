package com.codergang.directchat.ui.util

import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*



fun EditText.onChange(onChange: (text: Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            onChange(p0)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    })
    this.setOnKeyListener { _, p1, p2 ->
        if (text == null || text.isEmpty()) {
            if (p1 == KeyEvent.KEYCODE_DEL && p2.action == KeyEvent.ACTION_UP) {
                onChange(text)
            }
        }
        false
    }
}

fun showSnackBar(root: View, text: String){
    Snackbar.make(root, text, Snackbar.LENGTH_LONG).show()
}

private class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

private val format = "MMM dd yyyy"
var sdf: SimpleDateFormat = SimpleDateFormat(format, Locale("ms", "MY", "MY"))

fun Date.localizedString() = sdf.format(this)