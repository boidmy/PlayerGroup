package com.example.playergroup.ui.scrollselector

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.playergroup.R
import com.example.playergroup.custom.SliderLayoutManager
import com.example.playergroup.ui.scrollselector.ScrollSelectorBottomSheet.Companion.ScrollSelectorType.*
import com.example.playergroup.databinding.DialogSelectorBinding
import com.example.playergroup.util.VerticalMarginDecoration
import com.example.playergroup.util.click
import com.example.playergroup.util.toPx
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScrollSelectorBottomSheet: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogSelectorBinding::bind)
    private val viewModel: ScrollSelectorViewModel by viewModels()

    companion object {
        lateinit var callback: (String) -> Unit
        const val TYPE = "type"
        fun newInstance(type: ScrollSelectorType, callback: (String) -> Unit): ScrollSelectorBottomSheet =
            ScrollSelectorBottomSheet().apply {
                this@Companion.callback = callback
                arguments = Bundle().apply {
                    putSerializable(TYPE, type)
                }
            }

        enum class ScrollSelectorType {
            HEIGHT,
            WEIGHT,
            YEAROFBIRTH,
            SEX,
            POSITION
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
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.isDraggable = false
            behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> { }
                        BottomSheetBehavior.STATE_COLLAPSED -> { dismiss() }
                        BottomSheetBehavior.STATE_HIDDEN -> {}
                        BottomSheetBehavior.STATE_DRAGGING -> {}
                        BottomSheetBehavior.STATE_SETTLING -> {}
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        } ?: run {
            //TODO 예외 처리 ..?
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_selector, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewType = (arguments?.get(TYPE) as? ScrollSelectorType) ?: POSITION
        val selectorList = viewModel.getSelectorDataList(viewType)

        binding.title.text = viewModel.getSelectorTitle(viewType)

        binding.btnClose click { dismiss() }

        binding.selectorList.apply {
            layoutManager = SliderLayoutManager(requireContext()).apply {
                this.callback = object: SliderLayoutManager.OnItemSelectedListener {
                    override fun onItemSelected(layoutPosition: Int) {}
                }
            }

            adapter = SliderListAdapter().also {
                it.items = selectorList
                val defaultIndex = selectorList.size / 2
                smoothScrollToPosition(defaultIndex)
            }

            if (itemDecorationCount == 0)
                addItemDecoration(VerticalMarginDecoration(itemMargin = 13.toPx))
        }

        binding.btnConfirm click {
            val index = (binding.selectorList.layoutManager as? SliderLayoutManager)?.selectIndex ?: -1
            if (index == -1) dismiss()
            callback.invoke(selectorList.getOrNull(index) ?: "")
            dismiss()
        }
    }
}