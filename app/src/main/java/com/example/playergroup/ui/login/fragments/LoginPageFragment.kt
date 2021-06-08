package com.example.playergroup.ui.login.fragments

import android.os.Bundle
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
        initEditView()
        initBtnView()
        initViewModel()
    }

    private fun initEditView() {

    }

    private fun initBtnView() {
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
                if (activity?.currentFocus != null) {
                    hideKeyboard(activity?.currentFocus!!)
                }

                val id = etLoginId.text.toString()
                val pw = etLoginPw.text.toString()

                if (isEditTextEmpty(id, pw)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.input_empty_error)).show()
                } else if (!isEmailPattern(id)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.email_error_info)).show()
                } else {
                    loginViewModel.loadingProgress?.invoke(true)
                    loginViewModel.signInEmailLogin(id, pw)
                }
            }
        }
    }

    private fun initViewModel() {

    }

    override fun onPause() {
        super.onPause()
        binding.etLoginId.text = null
        binding.etLoginPw.text = null
    }
}