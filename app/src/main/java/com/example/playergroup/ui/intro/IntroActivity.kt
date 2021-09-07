package com.example.playergroup.ui.intro

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.R
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.api.AuthRepository
import com.example.playergroup.custom.DialogCustom
import com.example.playergroup.data.AppUpDateType
import com.example.playergroup.databinding.ActivityIntroBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.util.LandingRouter
import com.example.playergroup.util.appVersion
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity: BaseActivity<ActivityIntroBinding>() {

    private val authRepository by lazy { AuthRepository() }
    private val introViewModel by viewModels<IntroViewModel>()

    override fun getViewBinding(): ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        if (!isWIFIConnected()) {
            finishAlert(this@IntroActivity)
            return
        }
        initViewModel()
    }

    private fun initViewModel() {
        introViewModel.apply {
            firebaseAppVersionInfoResult.observe(this@IntroActivity, Observer {
                when (it.first) {
                    AppUpDateType.FORCED -> forcedAlert(it.second).show()
                    AppUpDateType.SELECT -> {
                        selectAlert(it.second) {
                            if (it) {
                                //todo 마켓으로 보내야 함
                                finish()
                            } else goToLanding()
                        }.show()
                    }
                    else -> goToLanding()
                }
            })

            firebaseUserDataResult.observe(this@IntroActivity, Observer {
                if (it?.isEmptyData() == true) {
                    LandingRouter.move(this@IntroActivity, RouterEvent(type = Landing.MY_PAGE, paramBoolean = true))
                } else {
                    PlayerGroupApplication.instance.userInfo = it
                    LandingRouter.move(this@IntroActivity, RouterEvent(type = Landing.MAIN))
                }
                finish()
            })

            getAppVersionInfo(appVersion)
        }
    }

    private fun goToLanding() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(300L) //todo 스플래시 이미지 보여주는 시간 인데 나중에 작업할때 다시 처리 해 봅시다.

            if (introViewModel.currentUser == null) {
                LandingRouter.move(this@IntroActivity, RouterEvent(type = Landing.LOGIN))
            } else {
                introViewModel.getUserProfile(introViewModel.currentUser?.email)
            }
        }
    }

    private fun Context.forcedAlert(msg: String): DialogCustom =
        DialogCustom(this)
            .setMessage(msg)
            .setConfirmBtnText(R.string.ok)
            .setDialogCancelable(false)
            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                override fun onClick(dialogCustom: DialogCustom) {
                    //todo 마켓으로 보내는 로직 추가 예정
                    dialogCustom.dismiss()
                    finish()
                }
            })

    private fun Context.selectAlert(msg: String, callback:(Boolean) -> Unit): DialogCustom =
        DialogCustom(this)
            .setMessage(msg)
            .setConfirmBtnText(R.string.ok)
            .showCancelBtn(true)
            .setCancelBtnText(R.string.cancel)
            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                override fun onClick(dialogCustom: DialogCustom) {
                    dialogCustom.dismiss()
                    callback.invoke(true)
                }
            })
            .setCancelClickListener(object: DialogCustom.DialogCustomClickListener {
                override fun onClick(dialogCustom: DialogCustom) {
                    dialogCustom.dismiss()
                    callback.invoke(false)
                }
            })
}