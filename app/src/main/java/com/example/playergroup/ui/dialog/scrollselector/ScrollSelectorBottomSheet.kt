package com.example.playergroup.ui.dialog.scrollselector

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
import com.example.playergroup.databinding.DialogSelectorBinding
import com.example.playergroup.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScrollSelectorBottomSheet: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogSelectorBinding::bind)
    private val viewModel: ScrollSelectorViewModel by viewModels()

    companion object {
        lateinit var callback: (String) -> Unit
        const val TYPE = "type"
        const val SELECT_ITEM = "selectText"
        const val SELECTED_ITEM = "selected_item"
        const val CUSTOM_TITLE = "custom_title"
        fun newInstance(
            type: ViewTypeConst,
            selectItem: String = "",
            customTitle: String = "",
            callback: (String) -> Unit
        ): ScrollSelectorBottomSheet =
            ScrollSelectorBottomSheet().apply {
                this@Companion.callback = callback
                arguments = Bundle().apply {
                    putSerializable(TYPE, type)
                    putSerializable(SELECT_ITEM, selectItem)
                }
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
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            dismiss()
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                        }
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

        val viewType = (arguments?.get(TYPE) as? ViewTypeConst) ?: ViewTypeConst.SCROLLER_POSITION
        val selectorList = viewModel.getSelectorDataList(viewType)
        val selectItem = arguments?.get(SELECT_ITEM)
        val customTitle = arguments?.getString(CUSTOM_TITLE) ?: ""

        binding.title.text =
            if (customTitle.isEmpty()) viewModel.getSelectorTitle(viewType) else customTitle

        binding.btnClose click { dismiss() }

        binding.selectorList.apply {
            layoutManager = SliderLayoutManager(requireContext()).apply {
                this.callback = object : SliderLayoutManager.OnItemSelectedListener {
                    override fun onItemSelected(layoutPosition: Int) {}
                }
            }

            adapter = SliderListAdapter().also {
                if (viewType == ViewTypeConst.SCROLLER_CATEGORY) {
                    it.items = selectorList
                }
                it.items = selectorList
                /*val defaultIndex = selectorList.size / 2*/
                var defaultIndex = 0
                for ((index, item) in selectorList.withIndex()) {
                    if (item == selectItem) {
                        defaultIndex = index
                        break
                    }
                }
                smoothScrollToPosition(defaultIndex)
                adapter = SliderListAdapter().apply {
                    items = selectorList

                    val selectedIndex = arguments?.getString(SELECTED_ITEM)

                    val index = if (selectedIndex.isNullOrEmpty()) {
                        selectorList.size / 2
                    } else {
                        selectorList.indexOfFirst { it == selectedIndex }
                    }

                    smoothScrollToPosition(index)
                }

                if (itemDecorationCount == 0)
                    addItemDecoration(VerticalMarginDecoration(itemMargin = 13.toPx))
            }

            binding.btnConfirm click {
                val index =
                    (binding.selectorList.layoutManager as? SliderLayoutManager)?.selectIndex ?: -1
                if (index == -1) dismiss()
                callback.invoke(selectorList.getOrNull(index) ?: "")
                dismiss()
            }
        }
    }
}