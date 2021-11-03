package com.example.playergroup.ui.club.holder

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.R
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ViewClubMemberItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.click
import com.example.playergroup.util.debugToast
import com.example.playergroup.util.viewBinding

class ClubMemberItemViewHolder(parent: ViewGroup): BaseViewHolder<ViewClubMemberItemBinding>(
    parent.viewBinding(ViewClubMemberItemBinding::inflate)) {
    override fun onBindView(data: Any?) {
        (data as? ClubMemberDataSet)?.let {
            initView(it)
        }
    }

    private fun initView(data: ClubMemberDataSet) {
        with (binding) {
            Glide.with(itemView.context)
                .load(data.img)
                .placeholder(R.drawable.icon_user)
                .into(userImg)

            name.text = data.name
            playPosition.text = data.playPosition
            stateIcon.visibility = if (data.email == PlayerGroupApplication.instance.userInfo?.email) View.VISIBLE else View.GONE
            btnApprove.visibility = if (data.isJoiningUser) View.VISIBLE else View.GONE

            btnApprove click {
                //todo 사용자에게 푸시 보내기
                //itemView.context debugToast { "준비중" }
                data.joinProgressMemberClickCallback?.invoke(data.email, true)
            }

            btnReject click {
                //todo 사용자에게 푸시 보내기
                //itemView.context debugToast { "준비중" }
                data.joinProgressMemberClickCallback?.invoke(data.email, false)
            }

            // Club Admin 에서만 [승인 or 거절] 버튼 노출 하기
            btnApprove.visibility = if(data.isAdmin) View.VISIBLE else View.GONE
            btnReject.visibility = if(data.isAdmin) View.VISIBLE else View.GONE

            // Club Member 에게만 노출 되는 영역
            tvJoinState.visibility = if (data.isAdmin) View.GONE else View.VISIBLE

            // Club Join 영역과 Member 영역을 나눔
            btnLayer.visibility = if (data.isJoiningUser) View.VISIBLE else View.GONE


        }

        itemView click {
            LandingRouter.move(itemView.context, RouterEvent(type = Landing.MY_PAGE, primaryKey = data.email))
        }
    }
}