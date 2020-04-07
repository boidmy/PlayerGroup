package com.example.playergroup.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class DisableSwipeViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    var enable = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (enable) { super.onTouchEvent(ev) } else {
            ev?.actionMasked != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (enable) {
            return super.onInterceptTouchEvent(ev)
        } else {
            if (ev?.actionMasked == MotionEvent.ACTION_MOVE) {

            } else {
                if (super.onInterceptTouchEvent(ev)) {
                    super.onTouchEvent(ev)
                }
            }
        }
        return false
    }

}