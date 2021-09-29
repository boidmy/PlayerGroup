package com.example.playergroup.ui.board.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.NoticeBoardCategory
import com.example.playergroup.databinding.ViewBoardCategoryItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.viewBinding

class NoticeCategoryAdapter(private val callback: (String) -> Unit) :
    RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var items: MutableList<NoticeBoardCategory> = mutableListOf()
        set(value) {
            field = value
            calculate(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return NoticeCategoryViewHolder(parent) as BaseViewHolder<ViewBinding>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        items.getOrNull(position)?.let { item ->
            holder.itemView.setOnClickListener {
                callback(item.categoryKey)
            }
            holder.onBindView(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun calculate(items: MutableList<NoticeBoardCategory>) {
        //해당 list는 diffutil이 현재로선 필요없음 필요하면 추가예정
        notifyDataSetChanged()
    }
}

class NoticeCategoryViewHolder(parent: ViewGroup) :
    BaseViewHolder<ViewBoardCategoryItemBinding>(parent.viewBinding(ViewBoardCategoryItemBinding::inflate)) {

    override fun onBindView(data: Any?) {
        (data as? NoticeBoardCategory)?.let {
            with(binding) {
                category.text = it.categoryNm
            }
        }
    }
}