package com.example.playergroup.ui.login

import android.content.Intent
import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playergroup.R
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.databinding.ActivityLoginBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.login.fragments.BottomSheetLoginFragment
import com.example.playergroup.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.reactivex.android.schedulers.AndroidSchedulers

class LoginActivity: BaseActivity<ActivityLoginBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    //Google Login Activity Result
    private lateinit var getGoogleSignResult: ActivityResultLauncher<Intent>

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
    override fun onNetworkStateLost(network: Network?) {
        //finishAlert(this@LoginActivity)
    }
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initGifImg()
        initActivityResult()
        initBtn()
        initViewModel()
    }

    private fun initViewModel() {
        loginViewModel.apply {
            googleLogin = this@LoginActivity::googleLogin

            // Google Login 성공 하게 되면 여기로 들어온다
            firebaseResult.observe(this@LoginActivity, Observer { loginResultCallback ->
                loadingProgress?.invoke(false)
                if (loginResultCallback.isSuccess) {
                    LandingRouter.move(this@LoginActivity, RouterEvent(loginResultCallback.landingType, paramBoolean = true))
                    finish()
                } else {
                    DialogCustom(this@LoginActivity)
                        .setMessage(R.string.dialog_alert_msg_error)
                        .setConfirmBtnText(R.string.ok)
                        .setDialogCancelable(false)
                        .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                            override fun onClick(dialogCustom: DialogCustom) {
                                dialogCustom.dismiss()
                            }
                        })
                        .show()
                }
            })

            firebaseJoinResult.observe(this@LoginActivity, Observer {
                if (it) {
                    loadingProgress?.invoke(false)
                    showDefDialog(getString(R.string.email_check)).show()
                    pagerMoveCallback?.invoke(LoginType.LOGIN.value)
                }
            })

            firebaseError.observe(this@LoginActivity, Observer {
                loadingProgress?.invoke(false)
                when (it) {
                    is Int -> showDefDialog(getString(it)).show()
                    is String -> showDefDialog(it).show()
                }
            })

            firebaseUserPasswordResult.observe(this@LoginActivity, Observer {
                loadingProgress?.invoke(false)
                val message = if (it) {
                    "비밀번호 변경 메일을 전송했습니다."
                } else {
                    "가입된 이메일이 없습니다. 다시 한번 확인해 주세요."
                }
                showDefDialog(message).show()
                if (it) dismiss?.invoke()
            })
        }
    }

    private fun initActivityResult() {
        getGoogleSignResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                    val token = account?.idToken
                    if (token.isNullOrEmpty()) {
                        debugToast { "Error > google 토큰값을 가져오지 못함" }
                    } else {
                        loginViewModel.firebaseAuthWithGoogle(token)
                    }
                } catch (e: ApiException) {
                    debugToast { "Google sign in failed > ${e.message}" }
                    Log.e("GOOGLE", "Error > $e")
                }
            }
    }

    private fun googleLogin() {
        LandingRouter.move(this, RouterEvent(type = Landing.GOOGLE_LOGIN, activityResult = getGoogleSignResult))
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
        val newInstance = BottomSheetLoginFragment.newInstance(login.value)
        if (newInstance.isVisible) return
        newInstance.show(supportFragmentManager, newInstance.tag)
    }

    private fun initGifImg() {
        Glide.with(this).asGif().load(R.drawable.intro1).into(binding.bgImg)
    }
}