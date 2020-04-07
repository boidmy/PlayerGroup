package com.example.playergroup.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

infix fun View?.click(block: (View) -> Unit) = this?.setOnClickListener(block)

infix fun Any?.hideKeyboard(currentFocus: View) {
    if (currentFocus is AppCompatEditText) {
        currentFocus.apply {
            clearFocus()
            requestFocus()
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}