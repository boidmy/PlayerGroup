package com.example.playergroup.join_login.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.example.playergroup.R
import com.example.playergroup.join_login.JoinLoginRxBus
import com.example.playergroup.util.DialogCustom
import com.example.playergroup.util.click
import com.example.playergroup.util.hideKeyboard
import kotlinx.android.synthetic.main.fragment_join.*
import java.util.regex.Pattern

class JoinPageFragment: Fragment() {

    companion object {
        fun newInstance() = JoinPageFragment().apply {
            arguments = Bundle()
        }
    }

    private val mRxBus by lazy { JoinLoginRxBus.getInstance() }
    private val PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$")   // 비밀번호 정규식

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_join, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_join click {

            if (activity?.currentFocus != null) {
                hideKeyboard(activity?.currentFocus!!)
            }

            if (isEditTextEmpty(et_join_id, et_join_pw)) {
                DialogCustom(it.context)
                    .setMessage(R.string.input_empty_error)
                    .setConfirmBtnText(R.string.ok)
                    .setDialogCancelable(false)
                    .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                        override fun onClick(dialogCustom: DialogCustom) {
                            dialogCustom.dismiss()
                        }
                    })
                    .show()
            } else {
                mRxBus.publisher_idpw(et_join_id.text.toString(), et_join_pw.text.toString())
                mRxBus.publisher_goTo(mRxBus.LOGINPAGE)
            }
        }

        tv_cancel click { mRxBus.publisher_goTo(mRxBus.LOGINPAGE) }
    }

    override fun onResume() {
        super.onResume()
    }

    // 화면을 벗어나면 editText 내용 삭제
    override fun onPause() {
        super.onPause()
        et_join_id.text = null
        et_join_pw.text = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun isEditTextEmpty(id: AppCompatEditText, pw: AppCompatEditText) =
        (id.text?.trim().isNullOrEmpty() || pw.text?.trim().isNullOrEmpty())
}