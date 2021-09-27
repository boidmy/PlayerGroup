package com.example.playergroup.ui.login.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.playergroup.R
import com.example.playergroup.databinding.FragmentSearchmemberinfoBinding
import com.example.playergroup.ui.login.LoginType
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
    }

    private fun initBtnView() {
        with (binding) {

            btnSearch click  {
                setSearchUser()
            }
            etSearchId.setOnKeyListener { _, keyCode, event ->
                if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    setSearchUser()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            tvLogin click {
                loginViewModel.pagerMoveCallback?.invoke(LoginType.LOGIN.value)
            }
        }
    }

    private fun setSearchUser() {
        with (binding) {
            hideKeyboard(etSearchId)
            val id = etSearchId.text.toString()
            loginViewModel.loadingProgress?.invoke(true)
            loginViewModel.searchUserPassword(id)
        }
    }
}