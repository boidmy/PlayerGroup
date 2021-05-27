package com.example.playergroup.join_login.fragments

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatEditText
import com.example.playergroup.R
import com.example.playergroup.fragments.BaseFragment
import com.example.playergroup.join_login.JoinLoginRxBus
import com.example.playergroup.util.click
import com.example.playergroup.util.hideKeyboard
import kotlinx.android.synthetic.main.fragment_join.tv_cancel
import kotlinx.android.synthetic.main.fragment_searchmemberinfo.*

class SearchMemberInfoPageFragment: BaseFragment() {

    private val mRxBus by lazy { JoinLoginRxBus.getInstance() }

    companion object {
        fun newInstance() = SearchMemberInfoPageFragment().apply {
            arguments = Bundle()
        }
    }

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_searchmemberinfo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_cancel click { mRxBus.publisher_goTo(mRxBus.LOGINPAGE) }

        btn_search click {
            if (activity?.currentFocus != null) {
                hideKeyboard(activity?.currentFocus!!)
            }
            val message =
                if (et_search_id.isEditTextEmpty())
                    it.context.getString(R.string.profile_edit_is_empty)
                else if (!et_search_id.isEmailPattern())
                    "이메일 형식이 아닙니다"
                else ""

            //todo https://firebase.google.com/docs/auth/android/passing-state-in-email-actions?hl=ko
            // 앱으로 리디렉션 하는 방법
            if (message.isEmpty()) {
                mRxBus.publisher_loading(true)
                firebaseAuth
                    .sendPasswordResetEmail(et_search_id.text.toString())
                    .addOnCompleteListener {
                        mRxBus.publisher_loading(false)
                        val message2 = if (it.isSuccessful) {
                            "비밀번호 변경 메일을 전송했습니다."
                        } else {
                            "가입된 이메일이 없습니다. 다시 한번 확인해 주세요."
                        }
                        showDefDialog(requireContext(), message2).show()
                        mRxBus.publisher_goTo(mRxBus.LOGINPAGE)
                    }
            } else {
                showDefDialog(it.context, message).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun AppCompatEditText.isEditTextEmpty() = this.text?.trim().isNullOrEmpty()

    private fun AppCompatEditText.isEmailPattern() =
        (Patterns.EMAIL_ADDRESS.matcher(this.text?.trim().toString()).matches())
}