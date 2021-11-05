package com.example.playergroup.util

import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.playergroup.R

interface TextWatcherUse : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}

fun TextView.textWatcher(drawable: GradientDrawable) = object : TextWatcherUse {
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0?.length ?: 0 > 0) {
            drawable.setColor(
                ContextCompat.getColor(
                context,
                R.color.btn_enabled_background
            ))
            setTextColor(
                ContextCompat.getColor(
                context,
                R.color.btn_enabled_text
            ))
            background = drawable
            isEnabled = true
        } else {
            drawable.setColor(
                ContextCompat.getColor(
                context,
                R.color.btn_disabled_background
            ))
            setTextColor(
                ContextCompat.getColor(
                context,
                R.color.btn_disabled_text
            ))
            background = drawable
            isEnabled = false
        }
    }
}

fun TextView.initTextWatcher(drawable: GradientDrawable) {
    setTextColor(ContextCompat.getColor(
        this.context,
        R.color.btn_disabled_text
    ))
    drawable.setColor(
        ContextCompat.getColor(
            this.context,
            R.color.btn_disabled_background
        )
    )
    background = drawable
}