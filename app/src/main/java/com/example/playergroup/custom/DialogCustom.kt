package com.example.playergroup.custom

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.playergroup.R
import com.example.playergroup.databinding.DialogCustomBinding

class DialogCustom constructor(context: Context) : AlertDialog(context, R.style.AlertDialogTheme), View.OnClickListener {
    private var mConfirmClickListener: DialogCustomClickListener? = null
    private var mCancelClickListener: DialogCustomClickListener? = null

    private val binding by lazy { DialogCustomBinding.inflate(layoutInflater) }

    interface DialogCustomClickListener {
        fun onClick(dialogCustom: DialogCustom)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.let {
            it.attributes?.windowAnimations = R.style.CustomDialogStyle
            it.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }

        initBtn()
    }

    private fun initBtn() {
        with (binding) {
            btnDialogPosit.setOnClickListener(this@DialogCustom)
            btnDialogNegative.setOnClickListener(this@DialogCustom)
        }
    }

    fun setMessage(text: String?): DialogCustom {
        binding.tvDialogDetail.apply {
            if (TextUtils.isEmpty(text)) visibility = View.GONE else {
                visibility = View.VISIBLE
                this.text = text
            }
        }
        return this
    }

    fun setMessage(@StringRes messageId: Int): DialogCustom {
        binding.tvDialogDetail.apply {
            val text = context.getText(messageId) as String?
            if (TextUtils.isEmpty(text)) visibility = View.GONE else {
                visibility = View.VISIBLE
                this.text = text
            }
        }
        return this
    }

    fun setConfirmBtnText(text: String?): DialogCustom {
        binding.tvPositive.text = if (text.isNullOrEmpty()) "확인" else text
        return this
    }

    fun setConfirmBtnText(@StringRes btnTxtId: Int): DialogCustom {
        val text = context.getText(btnTxtId) as String?
        binding.tvPositive.text = if (text.isNullOrEmpty()) "확인" else text
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
        binding.btnDialogNegative.visibility = if (isShow) View.VISIBLE else View.GONE
        return this
    }

    fun setCancelBtnText(text: String?): DialogCustom {
        binding.tvNegative.text = if (text.isNullOrEmpty()) "취소" else text
        return this
    }

    fun setDialogCancelable(isEnable: Boolean): DialogCustom {
        this.setCancelable(isEnable)
        return this
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btnDialogPosit) {
            mConfirmClickListener?.onClick(this)
        } else {
            mCancelClickListener?.onClick(this)
        }

    }
}