package com.example.playergroup.ui.dialog.calendar

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.R
import com.example.playergroup.ui.dialog.calendar.BaseCalendar.Companion.DAYS_OF_WEEK
import com.example.playergroup.databinding.ViewCalendarDayBinding
import com.example.playergroup.util.getSpannedColorText
import com.example.playergroup.ui.dialog.calendar.CalendarViewModel.CalendarData

class CalendarItemViewHolder(private val binding: ViewCalendarDayBinding): RecyclerView.ViewHolder(binding.root) {
    fun onBindView(data: CalendarData?) {
        with (binding) {

            tvDay.text = getSpannedColorText(
                origin = data?.date ?: "",
                changed = data?.date ?: "",
                color = if (data?.isSelected == true) {
                    Color.parseColor("#ffffff")
                } else {
                    if (data?.isToday == true) {
                        ContextCompat.getColor(itemView.context, R.color.colorPrimaryText)
                    } else {
                        if (adapterPosition % DAYS_OF_WEEK == 0) {
                            Color.parseColor("#e6230a")
                        } else {
                            ContextCompat.getColor(itemView.context, R.color.colorSecondaryText)
                        }
                    }
                },
                bold = true
            )

            tvToday.text =
                if (data?.isToday == true)
                    getSpannedColorText(
                        origin = itemView.context.getString(R.string.calendar_today),
                        changed = itemView.context.getString(R.string.calendar_today),
                        color = if (data.isSelected) {
                            Color.parseColor("#ffffff")
                        } else {
                            ContextCompat.getColor(itemView.context, R.color.colorSecondaryText)
                        },
                        bold = true
                    )
                else ""

            selectBg.setBackgroundColor(
                if (data?.isSelected == true) ContextCompat.getColor(itemView.context, R.color.colorSecondaryText)
                else 0
            )

        }
        itemView.isEnabled = data?.date?.isEmpty() != true
    }

}