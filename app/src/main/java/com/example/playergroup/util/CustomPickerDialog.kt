package com.example.playergroup.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import com.example.playergroup.R

class CustomPickerDialog constructor(context: Context) : androidx.appcompat.app.AlertDialog(context), View.OnClickListener {

    private var mConfirmClickListener: PickerDialogListener? = null
    private var mCancelClickListener: PickerDialogListener? = null

    private var mPic_height: NumberPicker
    private var mPic_weight: NumberPicker
    private var mPic_position: NumberPicker
    private var mBtn_ok: TextView
    private var mBtn_cancel: TextView


    interface PickerDialogListener {
        fun onClick(customPickerDialog: CustomPickerDialog, height: NumberPicker, weight: NumberPicker, position: NumberPicker)
    }

    init {
        val rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom_picker, null)
        window?.attributes?.windowAnimations = R.style.CustomDialogStyle

        mPic_height = rootView.findViewById(R.id.pic_height)
        mPic_weight = rootView.findViewById(R.id.pic_weight)
        mPic_position = rootView.findViewById(R.id.pic_position)
        mBtn_ok = rootView.findViewById(R.id.btn_ok)
        mBtn_cancel = rootView.findViewById(R.id.btn_cancel)

        mPic_height.apply {
            minValue = 155
            maxValue = 210
            wrapSelectorWheel = false   // Over Scroll Disable
            value = 180
        }

        mPic_weight.apply {
            minValue = 55
            maxValue = 120
            wrapSelectorWheel = false
            value = 70
        }

        mPic_position.apply {
            minValue = 1
            maxValue = 5
            displayedValues = arrayOf("포인트가드", "슈팅가드", "스몰포워드", "파워포워드", "센터")
            wrapSelectorWheel = false
            value = 3
        }

        mBtn_ok.setOnClickListener(this)
        mBtn_cancel.setOnClickListener(this)

        setView(rootView)

    }

    fun setConfirmClickListener(listener: PickerDialogListener): CustomPickerDialog {
        mConfirmClickListener = listener
        return this
    }

    fun setCancelClickListener(listener: PickerDialogListener): CustomPickerDialog {
        mCancelClickListener = listener
        return this
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_ok) {
            mConfirmClickListener?.onClick(this, mPic_height, mPic_weight, mPic_position)
        } else {
            mCancelClickListener?.onClick(this, mPic_height, mPic_weight, mPic_position)
        }
    }
}