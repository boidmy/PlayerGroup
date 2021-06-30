package com.example.playergroup.custom

import android.util.Log
import com.google.android.material.appbar.AppBarLayout

abstract class AppBarStateChangeListener: AppBarLayout.OnOffsetChangedListener {

    enum class AppBarState {
        EXPANDED,
        COLLAPSED
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val diff = (appBarLayout?.totalScrollRange ?: 0) + verticalOffset
        when {
            diff == 0 -> onStateChanged(appBarLayout, AppBarState.COLLAPSED)
            diff > 0 -> onStateChanged(appBarLayout, AppBarState.EXPANDED)
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: AppBarState)
}