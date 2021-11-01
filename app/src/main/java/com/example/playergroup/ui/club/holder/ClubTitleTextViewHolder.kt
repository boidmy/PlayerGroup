package com.example.playergroup.ui.club.holder

import android.view.ViewGroup
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.databinding.ViewClubMemberItemBinding
import com.example.playergroup.databinding.ViewClubTitleTextBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.viewBinding

class ClubTitleTextViewHolder(parent: ViewGroup) : BaseViewHolder<ViewClubTitleTextBinding>(
    parent.viewBinding(ViewClubTitleTextBinding::inflate)) {
    override fun onBindView(data: Any?) {
        (data as? ClubMemberDataSet)?.let {
            binding.tvTitle.text = it.titleText
        }
    }
}