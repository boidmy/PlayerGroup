package com.example.playergroup.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.playergroup.R
import com.google.android.material.button.MaterialButton

class DialogCustom constructor(context: Context) : AlertDialog(context, R.style.AlertDialogTheme), View.OnClickListener {

    private var mMessageView: TextView? = null
    private var mCancelBtn: MaterialButton? = null
    private var mConfirmBtn: MaterialButton? = null

    private var mMessageTxt: String? = null
    private var mConfirmTxt: String? = null
    private var mConfirmClickListener: DialogCustomClickListener? = null
    private var mCancelClickListener: DialogCustomClickListener? = null

    interface DialogCustomClickListener {
        fun onClick(dialogCustom: DialogCustom)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_custom)

        window?.let {
            it.attributes?.windowAnimations = R.style.CustomDialogStyle
            it.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }

        mMessageView = findViewById(R.id.tv_dialog__detail)
        mCancelBtn = findViewById(R.id.btn_dialog__negative)
        mConfirmBtn = findViewById(R.id.btn_dialog__posit)

        setMessage(mMessageTxt)
        showCancelBtn(false)
        setConfirmBtnText(mConfirmTxt)

        mConfirmBtn?.setOnClickListener(this)
        mCancelBtn?.setOnClickListener(this)

    }

    fun setMessage(text: String?): DialogCustom {
        mMessageTxt = text
        if (TextUtils.isEmpty(mMessageTxt)) mMessageView?.visibility = View.GONE else {
            mMessageView?.visibility = View.VISIBLE
            mMessageView?.text = mMessageTxt
        }
        return this
    }

    fun setMessage(@StringRes messageId: Int): DialogCustom {
        mMessageTxt = context.getText(messageId) as String?
        if (TextUtils.isEmpty(mMessageTxt)) mMessageView?.visibility = View.GONE else {
            mMessageView?.visibility = View.VISIBLE
            mMessageView?.text = mMessageTxt
        }
        return this
    }

    fun setConfirmBtnText(text: String?): DialogCustom {
        mConfirmBtn?.text = if (TextUtils.isEmpty(text)) "OK" else text
        return this
    }

    fun setConfirmBtnText(@StringRes btnTxtId: Int): DialogCustom {
        mConfirmTxt = context.getText(btnTxtId) as String?
        mConfirmBtn?.text = if (TextUtils.isEmpty(mConfirmTxt)) "OK" else mConfirmTxt
        return this
    }

    fun setConfirmClickListener(listener: DialogCustomClickListener): DialogCustom {
        mConfirmClickListener = listener
        return this
    }

    fun setCancelClickListener(listener: DialogCustomClickListener): DialogCustom {
        mCancelClickListener = listener
        return this
    }

    fun showCancelBtn(isShow: Boolean): DialogCustom {
        mCancelBtn?.visibility = if (isShow) View.VISIBLE else View.GONE
        return this
    }

    fun setCancelBtnText(text: String?): DialogCustom {
        mCancelBtn?.text = if (TextUtils.isEmpty(text)) "Cancel" else text
        return this
    }

    fun setDialogCancelable(isEnable: Boolean): DialogCustom {
        this.setCancelable(isEnable)
        return this
    }


    override fun onClick(v: View) {
        if (v.id == R.id.btn_dialog__posit) {
            mConfirmClickListener?.onClick(this)
        } else {
            mCancelClickListener?.onClick(this)
        }

    }
}