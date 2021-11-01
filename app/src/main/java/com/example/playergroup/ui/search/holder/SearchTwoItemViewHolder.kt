package com.example.playergroup.ui.search.holder

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ViewSearchTwoItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.click
import com.example.playergroup.util.viewBinding

class SearchTwoItemViewHolder(parent: ViewGroup):
    BaseViewHolder<ViewSearchTwoItemBinding>(parent.viewBinding(ViewSearchTwoItemBinding::inflate)){
    override fun onBindView(data: Any?) {
        (data as? ClubInfo)?.let {
            initView(it)
        }
    }

    private fun initView(data: ClubInfo) {
        with(binding) {
            Glide.with(itemView.context)
                .load(data.clubImg)
                .into(binding.img)

            tvName.text = data.clubName ?: ""
            tvDescription.text = "아직 설명이 없습니다....................."
            tvMemberCount.text = "1명"
            // @전까지 보여주기
            tvAdmin.text = data.clubAdmin?.run {
                val atIndex = this.indexOf("@", 0)
                substring(0, atIndex)
            } ?: ""
        }

        itemView click {
            LandingRouter.move(itemView.context, RouterEvent(type = Landing.CLUB_MAIN, primaryKey = data.clubPrimaryKey))
        }
    }
}