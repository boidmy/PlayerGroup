package com.example.playergroup.ui.board.boardDetail.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.databinding.ViewBoardReviewItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.CalendarUtil
import com.example.playergroup.util.diffUtilResult
import com.example.playergroup.util.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardDetailReviewAdapter : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var items: MutableList<NoticeBoardItem> = mutableListOf()
        set(value) {
            calculate(value) {
                field.clear()
                field.addAll(value)
                it.dispatchUpdatesTo(this)
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return BoardDetailReviewViewHolder(parent) as BaseViewHolder<ViewBinding>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        items.getOrNull(position)?.let {
            holder.onBindView(it)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun calculate(value: MutableList<NoticeBoardItem>, callback: (DiffUtil.DiffResult) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffUtil = diffUtilResult(
                oldList = items,
                newList = value,
                itemCompare = { o, n -> o?.key == n?.key},
                contentCompare = { o, n -> o == n }
            )
            CoroutineScope(Dispatchers.Main).launch {
                callback(diffUtil)
            }
        }
    }
}

class BoardDetailReviewViewHolder(parent: ViewGroup) :
    BaseViewHolder<ViewBoardReviewItemBinding>(parent.viewBinding(ViewBoardReviewItemBinding::inflate)) {

    override fun onBindView(data: Any?) {
        (data as? NoticeBoardItem)?.let {
            with(binding) {
                reviewEditTime.text = CalendarUtil.getDateFormat(it.time)
                reviewSub.text = it.sub
            }
        }
    }
}