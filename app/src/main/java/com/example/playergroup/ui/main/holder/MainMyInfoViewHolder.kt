package com.example.playergroup.ui.main.holder

import android.view.View
import android.view.ViewGroup
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ViewMainMyInfoBinding
import com.example.playergroup.ui.base.BaseViewHolder
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.click
import com.example.playergroup.util.debugToast
import com.example.playergroup.util.viewBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * 메인 화면 탑 고정 영역
 */
class MainMyInfoViewHolder(parent: ViewGroup):
    BaseViewHolder<ViewMainMyInfoBinding>(parent.viewBinding(ViewMainMyInfoBinding::inflate)) {

    private val pgApplication by lazy { PlayerGroupApplication.instance }

    override fun onBindView(data: Any?) {

        with (binding) {
            if (pgApplication.isLogin()) {
                //todo 프로필 이미지 Full Url 저장 하는 로직 구현 후 여기 구현 하자
                tvName.text = pgApplication.userInfo?.name ?: ""
                tvAttendanceRate.text = "-"
            } else {
                tvName.text = "로그인이 필요합니다."
                tvAttendanceRate.visibility = View.GONE
            }

            llMyImg click {
                LandingRouter.move(itemView.context, RouterEvent(Landing.MY_PAGE))
            }

            tvName click {
                LandingRouter.move(itemView.context, RouterEvent(Landing.MY_PAGE))
            }

            tvSearch click {
                LandingRouter.move(itemView.context, RouterEvent(Landing.SEARCH))
            }

            tvSetting click {
                LandingRouter.move(itemView.context, RouterEvent(Landing.SETTING))
            }

            tvMySchedule click {
                //todo 내 일정 보여줄 수 있는 화면 만들기 ( 동호회 참가 하기로 한 일정 노출 )
                itemView.context debugToast {"준비중입니다."}
            }

            tvMyClubs click {
                //todo 내가 가입한 클럽 보여주는 화면 만들기
                itemView.context debugToast {"준비중입니다."}
            }
        }
    }
}