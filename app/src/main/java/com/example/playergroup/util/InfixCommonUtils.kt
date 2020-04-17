package com.example.playergroup.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

const val REQUEST_CODE_GALLERY = 1000

infix fun View?.click(block: (View) -> Unit) = this?.setOnClickListener(block)

infix fun Context?.toastShort(message: () -> String) = Toast.makeText(this, message(), Toast.LENGTH_SHORT).show()

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

// Fragment Navigation
infix fun View?.goTo(@IdRes resId: Int) = this?.findNavController()?.navigate(resId)

infix fun Any?.placeHolder(context: Context) {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
}