package com.example.playergroup.join_login.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_join.*
import java.util.regex.Pattern

class JoinPageFragment: Fragment() {

    companion object {
        fun newInstance() = JoinPageFragment().apply {
            arguments = Bundle()
        }
    }

    private val mRxBus by lazy { JoinLoginRxBus.getInstance() }
    //private val PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$")   // 8 ~ 16 가능 문자 특수문자 모두 가능
    private val PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{8,15}.\$")   // 8 ~ 16 ( 특수문자, 문자, 숫자 모두 포함 )

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


        et_join_id.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // Focus 가 없어졌을 때 비교
                if ((v as AppCompatEditText).text?.trim().isNullOrEmpty()) {
                    ll_join_id.error = null
                } else {
                    if (isEmailPattern(v)) {
                        ll_join_id.error = null
                    } else {
                        ll_join_id.error = "이메일 형식이 아닙니다"
                    }
                }
            }
        }

        btn_join click {

            if (activity?.currentFocus != null) {
                hideKeyboard(activity?.currentFocus!!)
            }

            if (isEditTextEmpty(et_join_id, et_join_pw)) {
                showDialog(it.context, it.context.getString(R.string.input_empty_error)).show()
            } else if (!isPWDPattern(et_join_pw)) {
                showDialog(it.context, it.context.getString(R.string.input_pw_error)).show()
            } else {
                // 회원가입 진행
                FirebaseAuth
                    .getInstance()
                    .createUserWithEmailAndPassword(et_join_id.text.toString(), et_join_pw.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mRxBus.publisher_idpw(et_join_id.text.toString(), et_join_pw.text.toString())
                            mRxBus.publisher_goTo(mRxBus.LOGINPAGE)
                        } else {
                            showDialog(it.context, it.context.getString(R.string.dialog_alert_msg_error))
                        }
                    }
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
        ll_join_id.error = null
        et_join_id.text = null
        et_join_pw.text = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun isEditTextEmpty(id: AppCompatEditText, pw: AppCompatEditText) =
        (id.text?.trim().isNullOrEmpty() || pw.text?.trim().isNullOrEmpty())

    private fun isEmailPattern(et: AppCompatEditText) =
        (Patterns.EMAIL_ADDRESS.matcher(et.text?.trim().toString()).matches())

    private fun isPWDPattern(et: AppCompatEditText) =
        (PASSWORD_PATTERN.matcher(et.text?.toString()).matches())

    private fun showDialog(context: Context, msg: String): DialogCustom =
        DialogCustom(context)
            .setMessage(msg)
            .setConfirmBtnText(R.string.ok)
            .setDialogCancelable(false)
            .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                override fun onClick(dialogCustom: DialogCustom) {
                    dialogCustom.dismiss()
                }
            })
}