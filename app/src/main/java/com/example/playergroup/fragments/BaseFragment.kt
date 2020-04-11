package com.example.playergroup.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.playergroup.R
import com.example.playergroup.util.DialogCustom
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable

open class BaseFragment: Fragment() {

    protected val compositeDisposable = CompositeDisposable()
    protected val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    protected fun showDialog(context: Context, msg: String): DialogCustom =
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