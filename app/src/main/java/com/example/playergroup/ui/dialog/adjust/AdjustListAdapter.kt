package com.example.playergroup.ui.dialog.adjust

import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.getAction
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.AdjustDataSet
import com.example.playergroup.databinding.ViewAdjustItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.viewBinding

class AdjustListAdapter(
    private val touchCallback: ((RecyclerView.ViewHolder) -> Unit)
): ListAdapter<AdjustDataSet, BaseViewHolder<ViewBinding>>(
    object: DiffUtil.ItemCallback<AdjustDataSet>() {
        override fun areItemsTheSame(oldItem: AdjustDataSet, newItem: AdjustDataSet): Boolean =
            oldItem.viewType == newItem.viewType
        override fun areContentsTheSame(oldItem: AdjustDataSet, newItem: AdjustDataSet): Boolean =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> =
        ItemViewHolder(parent) as BaseViewHolder<ViewBinding>
    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(currentList?.getOrNull(position))
        holder.itemView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                touchCallback.invoke(holder)
            }
            false
        }
    }

    fun moveItem(from: Int, to: Int) {
        val list = currentList.toMutableList()
        val fromLocation = list[from]
        list.removeAt(from)
        if (to < from) {
            list.add(to + 1 , fromLocation)
        } else {
            list.add(to - 1, fromLocation)
        }
        submitList(list)
    }

    inner class ItemViewHolder(parent: ViewGroup)
        : BaseViewHolder<ViewAdjustItemBinding>(parent.viewBinding(ViewAdjustItemBinding::inflate)) {
        override fun onBindView(data: Any?) {
            (data as? AdjustDataSet)?.let {
                with (binding) {
                    tvTitle.text = it.title
                    tvSubTitle.text = it.subTitle
                }
            }
        }
    }
}