package com.example.playergroup.ui.dialog.management

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.R
import com.example.playergroup.databinding.DialogManagementBinding
import com.example.playergroup.util.HorizontalMarginDecoration
import com.example.playergroup.util.toPx
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManagementBottomSheet: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogManagementBinding::bind)
    private val viewModel: ManagementViewModel by viewModels()

    companion object {
        fun newInstance(): ManagementBottomSheet =
            ManagementBottomSheet().apply {
                //todo arguments..
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
        bottomSheet?.setBackgroundColor(Color.TRANSPARENT) ?: run {
            //TODO 예외 처리 ..?
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_management, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initObserver() {
        val makeClubList = PlayerGroupApplication.instance.userInfo?.clubAdmin
        val involvedClubList = PlayerGroupApplication.instance.userInfo?.clubInvolved

        viewModel.managementLiveData.observe(requireActivity(), Observer {
            (binding.makeClubList.adapter as? ManagementListAdapter)?.submitList(it.first)
            (binding.involvedClubList.adapter as? ManagementListAdapter)?.submitList(it.second)
        })

        viewModel.getClubList(makeClubList, involvedClubList)
    }

    private fun initView() {
        with(binding) {
            makeClubList.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = ManagementListAdapter {
                    dismiss()
                }
                if (itemDecorationCount == 0) addItemDecoration(HorizontalMarginDecoration(0, 18.toPx, 18.toPx))
            }
            involvedClubList.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = ManagementListAdapter {
                    dismiss()
                }
                if (itemDecorationCount == 0) addItemDecoration(HorizontalMarginDecoration(0, 18.toPx, 18.toPx))
            }
        }
    }
}