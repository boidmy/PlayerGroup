package com.example.playergroup.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 *
 * 문장을 단어 로 쪼갠다. 정규식 이용.
 * TextView 는 문장으로 인식 되면 개행이 마지막 문장부터 개행 되기 때문에
 * 레이아웃에 끝이 아닌 곳에서도 개행이 되는 문제가 있음.
 * 그래서 문장이 아닌 단어로 쪼개면 텍스트뷰 레이아웃 끝까지 도달하고 개행이 된다.
 */

class CharWrapTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(getCharWrapStr(text.toString()), type)
    }

    private fun getCharWrapStr(str: String): String {
        return str.replace(".(?!$)".toRegex(), "$0\u200b")
    }
}