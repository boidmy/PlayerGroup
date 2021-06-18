package com.example.playergroup.custom.calendar

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playergroup.R
import com.example.playergroup.custom.calendar.BaseCalendar.Companion.DAYS_OF_WEEK
import com.example.playergroup.databinding.DialogCalendarBottomSheetBinding
import com.example.playergroup.util.click
import com.example.playergroup.util.setItemAnimatorDuration
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CalendarPickerBottomSheet: BottomSheetDialogFragment() {
    companion object {
        lateinit var callback: (String) -> Unit
        fun newInstance(callback: (String) -> Unit): CalendarPickerBottomSheet =
            CalendarPickerBottomSheet().apply {
                this@Companion.callback = callback
                arguments = Bundle().apply {}
            }
    }

    private val binding by viewBinding(DialogCalendarBottomSheetBinding::bind)
    private val calendarViewModel by viewModels<CalendarViewModel>()

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
    ): View? = inflater.inflate(R.layout.dialog_calendar_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        with (binding) {
            btnConfirm click {
                val selectDate = calendarViewModel.getSelectedDate()
                //Toast.makeText(requireContext(), selectDate, Toast.LENGTH_SHORT).show()
                callback.invoke(selectDate)
                dismiss()
            }

            ivYearBefore click {
                calendarViewModel.setBeforeYear()
            }

            ivYearAfter click {
                calendarViewModel.setAfterYear()
            }

            ivMonthBefore click {
                calendarViewModel.setBeforeMonth()
            }

            ivMonthAfter click {
                calendarViewModel.setAfterMonth()
            }

            calendarList.apply {
                layoutManager = GridLayoutManager(requireContext(), DAYS_OF_WEEK)
                adapter = CalendarListAdapter()
                setItemAnimatorDuration(100L)
            }
        }
    }

    private fun getCalendarList() = (binding.calendarList.adapter as? CalendarListAdapter)?.items

    private fun initViewModel() {
        calendarViewModel.apply {
            getCalendarDataSet = this@CalendarPickerBottomSheet::getCalendarList
            setCalendarData()

            calendarCurrentDateLiveData.observe(viewLifecycleOwner, Observer {
                binding.tvCurrentDate.text = it
            })

            calendarListLiveData.observe(viewLifecycleOwner, Observer {
                (binding.calendarList.adapter as? CalendarListAdapter)?.items = it
            })
        }
    }
}