package com.example.playergroup.ui.intro

import android.net.Network
import android.os.Bundle
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.api.AuthRepository
import com.example.playergroup.databinding.ActivityIntroBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.LandingRouter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity: BaseActivity<ActivityIntroBinding>() {

    private val authRepository by lazy { AuthRepository() }

    override fun getViewBinding(): ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)
    override fun onNetworkStateLost(network: Network?) {
        //finishAlert(this@IntroActivity)
    }
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        if (!isWIFIConnected()) {
            finishAlert(this@IntroActivity)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000L)
            init()
        }
    }

    private fun init() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            LandingRouter.move(this@IntroActivity, RouterEvent(type = Landing.LOGIN))
        } else {
            //프로필 저장되어있는지 확인
            authRepository.isUserInfoEmpty {
                if (it) {
                    LandingRouter.move(this@IntroActivity, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
                } else {
                    LandingRouter.move(this@IntroActivity, RouterEvent(type = Landing.MAIN))
                }
                finish()
            }
        }
    }
}