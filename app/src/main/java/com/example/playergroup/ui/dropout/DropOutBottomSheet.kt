package com.example.playergroup.ui.dropout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playergroup.R
import com.example.playergroup.data.repository.AuthRepository
import com.example.playergroup.databinding.DialogDropOutBinding
import com.example.playergroup.util.DialogCustom
import com.example.playergroup.util.click
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth

class DropOutBottomSheet: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogDropOutBinding::bind)
    private val authRepository by lazy { AuthRepository() }

    companion object {
        lateinit var callback: () -> Unit
        fun newInstance(callback: () -> Unit): DropOutBottomSheet =
            DropOutBottomSheet().apply {
                Companion.callback = callback
                arguments = Bundle().apply {}
            }
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { setupBottomSheet(it) }
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        bottomSheetDialog.setCanceledOnTouchOutside(true)  // outside Touch Dismiss Disable
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
        } ?: run {
            //TODO 예외 처리 ..?
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_drop_out, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.agreeCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.btnDropOut.isEnabled = isChecked
        }

        binding.btnDropOut click {
            DialogCustom(requireContext())
                .setMessage(R.string.service_drop_out_info)
                .setConfirmBtnText(R.string.ok)
                .showCancelBtn(true)
                .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) {
                        authRepository.setFirebaseUserInfoDelete {
                            if (it) callback.invoke()
                            dialogCustom.dismiss()
                        }
                    }
                })
                .setCancelBtnText(getString(R.string.cancel))
                .setCancelClickListener(object: DialogCustom.DialogCustomClickListener {
                    override fun onClick(dialogCustom: DialogCustom) { dialogCustom.dismiss() }
                })
                .show()
        }
    }
}