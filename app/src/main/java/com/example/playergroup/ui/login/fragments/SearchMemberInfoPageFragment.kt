package com.example.playergroup.ui.login.fragments

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentLoginBinding
import com.example.playergroup.databinding.FragmentSearchmemberinfoBinding
import com.example.playergroup.ui.login.LoginViewModel
import com.example.playergroup.util.*

class SearchMemberInfoPageFragment: Fragment() {

    private val loginViewModel by activityViewModels<LoginViewModel>()
    private val binding by viewBinding(FragmentSearchmemberinfoBinding::bind)

    companion object {
        fun newInstance() = SearchMemberInfoPageFragment().apply {
            arguments = Bundle()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_searchmemberinfo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtnView()
        initViewModel()
    }

    private fun initViewModel() {
        loginViewModel.firebaseUserPasswordResult.observe(viewLifecycleOwner, Observer {
            loginViewModel.loadingProgress?.invoke(false)
            val message = if (it) {
                "비밀번호 변경 메일을 전송했습니다."
            } else {
                "가입된 이메일이 없습니다. 다시 한번 확인해 주세요."
            }
            requireContext().showDefDialog(message).show()
            if (it) loginViewModel.dismiss?.invoke()
        })
    }

    private fun initBtnView() {
        with (binding) {
            tvCancel click {
                loginViewModel.dismiss?.invoke()
            }

            btnSearch click  {
                hideKeyboard(etSearchId)

                val id = etSearchId.text.toString()

                if (isEditTextEmpty(id)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.input_search_email_empty_error)).show()
                } else if (!isEmailPattern(id)) {
                    requireContext().showDefDialog(requireContext().getString(R.string.email_error_info)).show()
                } else {
                    loginViewModel.loadingProgress?.invoke(true)
                    loginViewModel.searchUserPassword(id)
                }
            }
        }
    }
}