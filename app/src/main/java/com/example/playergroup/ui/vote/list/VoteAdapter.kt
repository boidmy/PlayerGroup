package com.example.playergroup.ui.vote.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.databinding.VoteListItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.viewBinding

class VoteAdapter(private val callback: (Int) -> Unit) : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var item: List<VoteModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return VoteViewHolder(parent) as BaseViewHolder<ViewBinding>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        item.getOrNull(position)?.let { item ->
            holder.itemView.setOnClickListener {
                item.sequence?.let { seq ->
                    callback(seq)
                }
            }
            holder.onBindView(item)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    fun setData(item: List<VoteModel>) {
        this.item = item
        notifyDataSetChanged()
    }
}

class VoteViewHolder(parent: ViewGroup) :
    BaseViewHolder<VoteListItemBinding>(parent.viewBinding(VoteListItemBinding::inflate)) {

    override fun onBindView(data: Any?) {
        (data as VoteModel).let { item ->
            with(binding) {
                title.text = item.title
                title.tag = item.sequence
                sub1.text = item.voteData.getOrNull(0)?.voteName
                sub2.text = item.voteData.getOrNull(1)?.voteName

            }
        }
    }
}