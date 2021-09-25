package com.example.playergroup.ui.vote.votePick.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.playergroup.data.room.VoteModel
import com.example.playergroup.data.room.VoteReview
import com.example.playergroup.databinding.ViewVoteMessageItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.viewBinding

class VotePickReviewAdapter : RecyclerView.Adapter<BaseViewHolder<ViewBinding>>() {

    var item: VoteModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return VotePickReviewViewHolder(parent) as BaseViewHolder<ViewBinding>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        holder.onBindView(item?.review?.getOrNull(position))
    }

    override fun getItemCount(): Int {
        return item?.review?.size ?: 0
    }

    fun setData(item: VoteModel) {
        this.item = item
        notifyDataSetChanged()
    }
}

class VotePickReviewViewHolder(parent: ViewGroup) :
    BaseViewHolder<ViewVoteMessageItemBinding>(parent.viewBinding(ViewVoteMessageItemBinding::inflate)) {

    override fun onBindView(data: Any?) {
        (data as VoteReview).let {
            with(binding) {
                message.text = it.review
                userName.text = it.name
            }
        }
    }
}