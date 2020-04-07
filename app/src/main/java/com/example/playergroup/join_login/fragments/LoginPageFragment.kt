package com.example.playergroup.join_login.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.example.playergroup.MainActivity
import com.example.playergroup.R
import com.example.playergroup.join_login.JoinLoginActivity
import com.example.playergroup.join_login.JoinLoginRxBus
import com.example.playergroup.util.DialogCustom
import com.example.playergroup.util.click
import com.example.playergroup.util.hideKeyboard
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_join.*
import kotlinx.android.synthetic.main.fragment_login.*

class LoginPageFragment: Fragment() {

    companion object {
        fun newInstance() = LoginPageFragment().apply {
            arguments = Bundle()
        }
    }

    private val mRxBus by lazy { JoinLoginRxBus.getInstance() }
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        compositeDisposable.add(mRxBus
            .listen_idpw()
            .subscribe {
                et_login_id.setText(it.id)
                et_login_pw.setText(it.pw)
            })

        tv_join click { mRxBus.publisher_goTo(mRxBus.JOINPAGE)}
        tv_search_member click { mRxBus.publisher_goTo(mRxBus.SEARCHMEMBERINFO) }

        btn_login click {

            if (activity?.currentFocus != null) {
                hideKeyboard(activity?.currentFocus!!)
            }

            if (isEditTextEmpty(et_login_id, et_login_pw)) {
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
                // TODO Loading Bar 구현 하기.
            }
        }

        //TODO 로그인 기능 구현 완료시 삭제 ( Test 로 메인에 진입 할 수 있게 연결한 것 )
        btn_login.setOnLongClickListener {
            mRxBus.publisher_goTo(mRxBus.GOMAIN)
            true
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        et_login_id.text = null
        et_login_pw.text = null
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun isEditTextEmpty(id: AppCompatEditText, pw: AppCompatEditText) =
        (id.text?.trim().isNullOrEmpty() || pw.text?.trim().isNullOrEmpty())
}