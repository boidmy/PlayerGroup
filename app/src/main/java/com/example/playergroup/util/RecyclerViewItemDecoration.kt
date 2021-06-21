package com.example.playergroup.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION

class HorizontalMarginDecoration(
    var itemMargin: Int = 0,
    var firstItemMargin: Int = 0,
    var lastItemMargin: Int = 0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        if (itemPosition == NO_POSITION) {
            val itemLayoutPosition = parent.getChildLayoutPosition(view)
            setOffset(itemLayoutPosition, itemCount, outRect)
        } else {
            setOffset(itemPosition, itemCount, outRect)
        }
    }

    private fun setOffset(itemIndex: Int, itemCount: Int, outRect: Rect) {
        if (itemIndex == 0) {    // 첫 번째 아이템
            outRect.set(firstItemMargin, 0, (itemMargin / 2), 0)
        } else if (itemCount > 0 && itemIndex == itemCount - 1) {  // 마지막 아이템
            outRect.set((itemMargin / 2), 0, lastItemMargin, 0)
        } else {  // 나머지
            outRect.set((itemMargin / 2), 0, (itemMargin / 2), 0)
        }
    }
}

class VerticalMarginDecoration(
    var itemMargin: Int = 0,
    var firstItemMargin: Int = 0,
    var lastItemMargin: Int = 0
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemCount = state.itemCount
        var itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == NO_POSITION) { itemPosition = parent.getChildLayoutPosition(view) }

        if (itemPosition == 0) {    // 첫 번째 아이템
            outRect.set(0, firstItemMargin, 0, (itemMargin / 2))
        } else if (itemCount > 0 && itemPosition == itemCount - 1) {  // 마지막 아이템
            outRect.set(0, (itemMargin / 2), 0, lastItemMargin)
        } else {  // 나머지
            outRect.set(0, (itemMargin / 2), 0, (itemMargin / 2))
        }
    }
}