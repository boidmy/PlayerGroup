package com.example.playergroup.ui.login

import android.net.Network
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.example.playergroup.R
import com.example.playergroup.databinding.ActivityLoginBinding
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.login.fragments.BottomSheetLoginFragment
import com.example.playergroup.util.click
import io.reactivex.android.schedulers.AndroidSchedulers

class LoginActivity: BaseActivity<ActivityLoginBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
    override fun onNetworkStateLost(network: Network?) {
        finishAlert(this@LoginActivity)
    }
    override fun onCreateBindingWithSetContentView(savedInstanceState: Bundle?) {
        initGifImg()
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
        val newInstance = BottomSheetLoginFragment.newInstance(login.value)
        newInstance.show(supportFragmentManager, newInstance.tag)
    }

    private fun initGifImg() {
        Glide.with(this).asGif().load(R.drawable.intro1).into(binding.bgImg)
    }
}