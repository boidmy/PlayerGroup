package com.example.playergroup.custom

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

open class IncludeBorderLineTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        val sb = SpannableStringBuilder(text)
        sb.setSpan(UnderlineSpan(), 0, text?.length ?: 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        super.setText(sb, type)
    }
}