package com.example.playergroup.ui.login.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.playergroup.R
import com.example.playergroup.databinding.DialogLoginContainerBinding
import com.example.playergroup.ui.login.JoinLoginAdapter
import com.example.playergroup.ui.login.LoginType
import com.example.playergroup.ui.login.LoginViewModel
import com.example.playergroup.util.getScreenHeightToPx
import com.example.playergroup.util.showDefDialog
import com.example.playergroup.util.toPx
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers

class BottomSheetLoginFragment: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogLoginContainerBinding::bind)
    private val loginViewModel by activityViewModels<LoginViewModel>()

    companion object {
        fun newInstance(tabPosition: Int = 0): BottomSheetLoginFragment =
            BottomSheetLoginFragment().apply {
                arguments = Bundle().apply {
                    putInt(TAB_POSITION, tabPosition)
                }
            }
        const val TAB_POSITION = "tab_position"
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { setupBottomSheet(it) }
        dialog.setOnDismissListener { getLoadingProgress(false) }
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheetDialog.setCanceledOnTouchOutside(false)  // outside Touch Dismiss Disable
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
            val behavior = BottomSheetBehavior.from(it)
            behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        } ?: run {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_login_container, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        loginViewModel.apply {
            loadingProgress = this@BottomSheetLoginFragment::getLoadingProgress
            dismiss = this@BottomSheetLoginFragment::getDismiss
            firebaseJoinResult.observe(viewLifecycleOwner, Observer {
                if (it) {
                    loginViewModel.loadingProgress?.invoke(false)
                    requireContext().showDefDialog(requireContext().getString(R.string.email_check)).show()
                    binding.pager.currentItem = LoginType.LOGIN.value
                }
            })
        }
    }

    private fun initView() {
        val index = arguments?.getInt(TAB_POSITION) ?: LoginType.LOGIN.value
        binding.pager.adapter = JoinLoginAdapter(childFragmentManager)
        binding.pager.apply {
            adapter = JoinLoginAdapter(childFragmentManager)
            setCurrentItem(index, false)
        }
    }

    private fun getDismiss() {
        dismiss()
    }
    private fun getLoadingProgress(isShow: Boolean) {
        binding.loadingProgress.publisherLoading(isShow)
    }
}