package com.example.playergroup.ui.vote.votePick.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.room.VoteData
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.databinding.ViewVotePickItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.viewBinding

class VotePickAdapter : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var item: VoteModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return VotePickViewHolder(parent) as BaseViewHolder<ViewBinding>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.itemView.setOnClickListener {
            item?.let { item ->
                setData(item.clickRadioBtn(position))
            }
        }
        holder.onBindView(item?.voteData?.getOrNull(position))
    }

    override fun getItemCount(): Int {
        return item?.voteData?.size ?: 0
    }

    fun setData(item: VoteModel) {
        this.item = item
        notifyDataSetChanged()
    }
}

class VotePickViewHolder(parent: ViewGroup) :
    BaseViewHolder<ViewVotePickItemBinding>(parent.viewBinding(ViewVotePickItemBinding::inflate)) {

    override fun onBindView(data: Any?) {
        (data as VoteData).let { item ->
            with(binding) {
                voteBtn.isChecked = item.checked
                voteName.text = item.voteName
                pickCount.text = item.count.toString()
            }
        }
    }
}