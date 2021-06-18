package com.example.playergroup.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class SliderLayoutManager(context: Context?) : LinearLayoutManager(context) {

    init {
         orientation = VERTICAL
    }

    var callback: OnItemSelectedListener? = null
    var selectIndex = 0
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        recyclerView = view!!

        // Smart snapping
        LinearSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        //scaleDownView()
        scaleDownHeightView()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleDownView()
            return scrolled
        } else {
            return 0
        }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            scaleDownHeightView()
            return scrolled
        } else {
            return 0
        }
    }

    private fun scaleDownHeightView() {
        val mid = height / 2.0f
        for (i in 0 until childCount) {

            // Calculating the distance of the child from the center
            val child = getChildAt(i) ?: return
            //val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
            val childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f
            val distanceFromCenter = Math.abs(mid - childMid)

            // The scaling formula
            val scale = 1-Math.sqrt((distanceFromCenter/height).toDouble()).toFloat()*0.66f

            // Set scale to view
            child.scaleX = scale
            child.scaleY = scale
        }
    }



    private fun scaleDownView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {

            // Calculating the distance of the child from the center
            val child = getChildAt(i) ?: return
            val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
            val distanceFromCenter = Math.abs(mid - childMid)

            // The scaling formula
            val scale = 1-Math.sqrt((distanceFromCenter/width).toDouble()).toFloat()*0.66f

            // Set scale to view
            child.scaleX = scale
            child.scaleY = scale
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // When scroll stops we notify on the selected item
        if (state.equals(RecyclerView.SCROLL_STATE_IDLE)) {

            /*// Find the closest child to the recyclerView center --> this is the selected item.
            val recyclerViewCenterX = getRecyclerViewCenterX()
            var minDistance = recyclerView.width
            var position = -1
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                var newDistance = Math.abs(childCenterX - recyclerViewCenterX)
                if (newDistance < minDistance) {
                    minDistance = newDistance
                    position = recyclerView.getChildLayoutPosition(child)
                }
            }*/

            val recyclerViewCenterY = getRecyclerViewCenterY()
            var minDistance = recyclerView.height
            var position = -1
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterY = getDecoratedTop(child) + (getDecoratedBottom(child) - getDecoratedTop(child)) / 2
                val test = (getDecoratedTop(child) - getDecoratedBottom(child)) / 2 + getDecoratedBottom(child)
                var newDistance = Math.abs(childCenterY - recyclerViewCenterY)
                if (newDistance < minDistance) {
                    minDistance = newDistance
                    position = recyclerView.getChildLayoutPosition(child)
                }
            }


            // Notify on item selection
            callback?.onItemSelected(position)
            selectIndex = position
        }
    }

    private fun getRecyclerViewCenterX() : Int {
        return (recyclerView.right - recyclerView.left)/2 + recyclerView.left
    }

    private fun getRecyclerViewCenterY(): Int = (recyclerView.bottom - recyclerView.top)/2

    interface OnItemSelectedListener {
        fun onItemSelected(layoutPosition: Int)
    }
}