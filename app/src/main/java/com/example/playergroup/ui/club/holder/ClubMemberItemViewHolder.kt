package com.example.playergroup.ui.club.holder

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.R
import com.example.playergroup.custom.DialogCustom
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ViewClubMemberItemBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.*

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
            myUserIconLayer.visibility = if (data.email == PlayerGroupApplication.instance.userInfo?.email || data.isAdmin) View.VISIBLE else View.GONE
            ivAdmin.visibility = if (data.isAdmin) View.VISIBLE else View.GONE
            btnApprove.visibility = if (data.isJoiningUser) View.VISIBLE else View.GONE

            btnApprove click {
                //todo 사용자에게 푸시 보내기
                data.joinProgressMemberClickCallback?.invoke(data.email, true)
            }

            btnReject click {
                //todo 사용자에게 푸시 보내기
                data.joinProgressMemberClickCallback?.invoke(data.email, false)
            }

            // Club Admin 에서만 [승인 or 거절] 버튼 노출 하기
            btnApprove.visibility = if(data.isCurrentUserAdmin) View.VISIBLE else View.GONE
            btnReject.visibility = if(data.isCurrentUserAdmin) View.VISIBLE else View.GONE

            // Club Member 에게만 노출 되는 영역
            tvJoinState.visibility = if (data.isCurrentUserAdmin) View.GONE else View.VISIBLE

            // Club Join 영역과 Member 영역을 나눔
            btnLayer.visibility = if (data.isJoiningUser) View.VISIBLE else View.GONE

        }

        itemView click {
            LandingRouter.move(itemView.context, RouterEvent(type = Landing.MY_PAGE, primaryKey = data.email))
        }

        // 클럽 장만 롱 클릭 가능
        itemView longClick {
            if (data.isCurrentUserAdmin && data.email != PlayerGroupApplication.instance.userInfo?.email) {
                DialogCustom(itemView.context)
                    .setMessage("[${data.name}]\n클럽 탈퇴를 진행할까요?")
                    .setConfirmBtnText(R.string.ok)
                    .showCancelBtn(true)
                    .setCancelBtnText(R.string.cancel)
                    .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                        override fun onClick(dialogCustom: DialogCustom) {
                            data.dropClubMemberClickCallback?.invoke(data.email)
                            dialogCustom.dismiss()
                        }
                    })
                    .setCancelClickListener(object: DialogCustom.DialogCustomClickListener {
                        override fun onClick(dialogCustom: DialogCustom) {
                            dialogCustom.dismiss()
                        }
                    })
                    .show()
            }
            true
        }
    }
}