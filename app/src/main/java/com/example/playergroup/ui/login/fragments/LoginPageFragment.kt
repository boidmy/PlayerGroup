package com.example.playergroup.ui.login.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentLoginBinding
import com.example.playergroup.ui.login.LoginType
import com.example.playergroup.ui.login.LoginViewModel
import com.example.playergroup.util.*

class LoginPageFragment: Fragment() {

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    companion object {
        fun newInstance() = LoginPageFragment().apply {
            arguments = Bundle()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with (binding) {
            tvJoin click {
                loginViewModel.pagerMoveCallback?.invoke(LoginType.JOIN.value)
            }
            tvSearchMember click {
                loginViewModel.pagerMoveCallback?.invoke(LoginType.LOGIN_INFO_LOST.value)
            }
            btnLoginGoogleLogin click {
                loginViewModel.loadingProgress?.invoke(true)
                loginViewModel.googleLogin?.invoke()
            }
            btnLogin click {
                setLoginEmailUser()
            }
            etLoginPw.setOnKeyListener { _, keyCode, event ->
                if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    setLoginEmailUser()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }

    private fun setLoginEmailUser() {
        with (binding) {
            hideKeyboard(etLoginPw)
            val id = etLoginId.text.toString()
            val pw = etLoginPw.text.toString()
            loginViewModel.loadingProgress?.invoke(true)
            loginViewModel.signInEmailLogin(id, pw)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.etLoginId.text = null
        binding.etLoginPw.text = null
    }
}