package com.example.playergroup.ui.login

import android.os.Bundle
import android.util.Log
import com.example.playergroup.data.Landing
import com.example.playergroup.data.LoginStateChange
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityLoginBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.login.fragments.BottomSheetLoginFragment
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.RxBus
import com.example.playergroup.util.click
import io.reactivex.rxkotlin.addTo

class InitLoginScreenActivity: BaseActivity<ActivityLoginBinding>() {

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initBtn()
        initObserver()
    }

    private fun initObserver() {
        RxBus.listen(LoginStateChange::class.java).subscribe {
            val userInfo = pgApplication.userInfo
            if (it.isLogin) {
                if (userInfo?.isEmptyData() == true) {
                    LandingRouter.move(this, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
                } else {
                    LandingRouter.move(this, RouterEvent(type = Landing.MAIN))
                }
            }
        }.addTo(compositeDisposable)
    }

    private fun initBtn() {
        with (binding) {
            tvContainerLogin click {
                LandingRouter.move(this@InitLoginScreenActivity,
                    RouterEvent(type = Landing.LOGIN, paramInt = LoginType.LOGIN.value))
            }

            btnContainerJoin click {
                LandingRouter.move(this@InitLoginScreenActivity,
                    RouterEvent(type = Landing.LOGIN, paramInt = LoginType.JOIN.value))
            }

            tvSkip click {
                LandingRouter.move(this@InitLoginScreenActivity, RouterEvent(type = Landing.MAIN))
            }
        }
    }
}