package com.example.playergroup.ui.login

import android.os.Bundle
import android.util.Log
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityLoginBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.login.fragments.BottomSheetLoginFragment
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.click

class InitLoginScreenActivity: BaseActivity<ActivityLoginBinding>() {

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initBtn()
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

    override fun onLoginStateChange(isLogin: Boolean) {
        val userInfo = pgApplication.userInfo
        if (isLogin) {
            if (userInfo?.isEmptyData() == true) {
                LandingRouter.move(this, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
            } else {
                LandingRouter.move(this, RouterEvent(type = Landing.MAIN))
            }
        }
    }
}