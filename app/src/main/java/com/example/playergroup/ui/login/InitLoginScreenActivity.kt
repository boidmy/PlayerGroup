package com.example.playergroup.ui.login

import android.os.Bundle
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
                showLoginPopup(LoginType.LOGIN)
            }

            btnContainerJoin click {
                showLoginPopup(LoginType.JOIN)
            }
        }
    }

    private fun showLoginPopup(login: LoginType) {
        val newInstance = BottomSheetLoginFragment.newInstance(login.value) { isSuccess ->
            if (isSuccess) LandingRouter.move(this, RouterEvent(type = Landing.MAIN))
        }
        if (newInstance.isVisible) return
        newInstance.show(supportFragmentManager, newInstance.tag)
    }

}