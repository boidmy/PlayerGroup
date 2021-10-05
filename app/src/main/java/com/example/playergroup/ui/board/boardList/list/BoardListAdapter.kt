package com.example.playergroup.ui.board.boardList.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.NoticeBoardCategory
import com.example.playergroup.data.NoticeBoardItem
import com.example.playergroup.databinding.ViewBoardCategoryItemBinding
import com.example.playergroup.databinding.ViewBoardListItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.diffUtilExtensions
import com.example.playergroup.util.viewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BoardListAdapter : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

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
        items.getOrNull(position)?.let {
            holder.onBindView(it)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun calculate(value: MutableList<NoticeBoardItem>, callback: (DiffUtil.DiffResult) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffUtil = this@BoardListAdapter.diffUtilExtensions(
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
            }
        }
    }
}