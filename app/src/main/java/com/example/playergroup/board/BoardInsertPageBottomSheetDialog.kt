package com.example.playergroup.board

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playergroup.R
import com.example.playergroup.util.getScreenHeightToPx
import com.example.playergroup.util.toPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BoardInsertPageBottomSheetDialog: BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): BoardInsertPageBottomSheetDialog =
            BoardInsertPageBottomSheetDialog().apply {
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
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheetDialog.setCanceledOnTouchOutside(false)  // outside Touch Dismiss Disable
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)

            //디바이스 높이값을 가져와서 헤더 높이 만큼 뺀 높이가 필터의 높이가 된다.
            val topMargin = 80.toPx
            val height = getScreenHeightToPx() - topMargin

            it.layoutParams.height = height
            val behavior = BottomSheetBehavior.from(it)
            behavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING ) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                override fun onSlide(p0: View, p1: Float) {}
            })
            //behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            it.requestLayout()

        } ?: run {
            //TODO 예외 처리 ..?
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_board_insert_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}