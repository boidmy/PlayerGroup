package com.example.playergroup.util

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

fun <T> diffUtilResult(
    oldList: MutableList<T>?,
    newList: MutableList<T>?,
    itemCompare: (T?, T?) -> Boolean,
    contentCompare: (T?, T?) -> Boolean
) = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList?.size ?: 0
    override fun getNewListSize(): Int = newList?.size ?: 0
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        itemCompare(oldList?.getOrNull(oldItemPosition), newList?.getOrNull(newItemPosition))
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        contentCompare(oldList?.getOrNull(oldItemPosition), newList?.getOrNull(newItemPosition))
})

fun RecyclerView.setItemAnimatorDuration(duration: Long): RecyclerView.ItemAnimator =
    DefaultItemAnimator().apply {
        addDuration = duration
        removeDuration = duration
        moveDuration = duration
        changeDuration = duration
    }
