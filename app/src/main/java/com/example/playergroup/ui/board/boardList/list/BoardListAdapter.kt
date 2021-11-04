package com.example.playergroup.ui.board.boardList.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.databinding.ViewBoardListItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.CalendarUtil
import com.example.playergroup.util.click
import com.example.playergroup.util.diffUtilResult
import com.example.playergroup.util.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardListAdapter(private val callback: (key: String) -> Unit) : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var items: MutableList<NoticeBoardItem> = mutableListOf()
        set(value) {
            calculate(value) {
                field.clear()
                field.addAll(value)
                it.dispatchUpdatesTo(this)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return BoardListViewHolder(parent) as BaseViewHolder<ViewBinding>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        items.getOrNull(position)?.let { item ->
            holder.itemView click {
                callback(item.key)
            }
            holder.onBindView(item)
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

class BoardListViewHolder(parent: ViewGroup) :
    BaseViewHolder<ViewBoardListItemBinding>(parent.viewBinding(ViewBoardListItemBinding::inflate)) {

    override fun onBindView(data: Any?) {
        (data as? NoticeBoardItem)?.let {
            with(binding) {
                boardTitle.text = it.title
                boardSub.text = it.sub
                boardTime.text = CalendarUtil.txtDate(it.time)
                userId.text = it.name
                reviewCount.text = it.reviewCount.toString()
            }
        }
    }
}