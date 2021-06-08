package com.example.playergroup.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentJoinBinding
import com.example.playergroup.ui.login.LoginViewModel
import com.example.playergroup.util.*

class JoinPageFragment: Fragment() {

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private val binding by viewBinding(FragmentJoinBinding::bind)

    companion object {
        fun newInstance() = JoinPageFragment().apply {
            arguments = Bundle()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_join, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditView()
        initBtnView()
        initViewModel()
    }

    private fun initEditView() {
        with(binding) {
            etJoinId.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    // Focus 가 없어졌을 때 비교
                    if ((v as AppCompatEditText).text?.trim().isNullOrEmpty()) {
                        llJoinId.error = null
                    } else {
                        val id = v.text.toString()
                        if (isEmailPattern(id)) {
                            llJoinId.error = null
                        } else {
                            llJoinId.error = requireContext().getString(R.string.email_error_info)
                        }
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        loginViewModel.apply {
            firebaseError.observe(viewLifecycleOwner, Observer {
                loginViewModel.loadingProgress?.invoke(false)
                requireContext().showDefDialog(it).show()
            })
        }
    }

    private fun initBtnView() {
        with (binding) {
            btnJoinGoogleLogin click {
                loginViewModel.loadingProgress?.invoke(true)
                loginViewModel.googleLogin?.invoke()
            }

            btnJoin click {
                if (activity?.currentFocus != null) {
                    hideKeyboard(activity?.currentFocus!!)
                }

                val id = etJoinId.text.toString()
                val pw = etJoinPw.text.toString()

                if (isEditTextEmpty(id, pw)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.input_empty_error)).show()
                } else if (!isEmailPattern(id)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.email_error_info)).show()
                } else if (!isPWDPattern(pw)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.input_pw_error)).show()
                } else {
                    loginViewModel.loadingProgress?.invoke(true)
                    loginViewModel.createEmailUserJoin(id, pw)
                }
            }
            tvCancel click {
                loginViewModel.dismiss?.invoke()
            }
        }
    }

    // 화면을 벗어나면 editText 내용 삭제
    override fun onPause() {
        super.onPause()
        with(binding) {
            llJoinId.error = null
            etJoinId.text = null
            etJoinPw.text = null
        }
    }
}