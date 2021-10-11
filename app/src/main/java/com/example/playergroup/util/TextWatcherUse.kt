package com.example.playergroup.util

import android.text.Editable
import android.text.TextWatcher

interface TextWatcherUse : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}