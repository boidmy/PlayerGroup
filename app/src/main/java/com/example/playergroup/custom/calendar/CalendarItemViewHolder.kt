package com.example.playergroup.custom.calendar

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.R
import com.example.playergroup.custom.calendar.BaseCalendar.Companion.DAYS_OF_WEEK
import com.example.playergroup.databinding.ViewCalendarDayBinding
import com.example.playergroup.util.getSpannedColorText
import com.example.playergroup.custom.calendar.CalendarViewModel.CalendarData

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
                        ContextCompat.getColor(itemView.context, R.color.textColor_High_Emphasis)
                    } else {
                        if (adapterPosition % DAYS_OF_WEEK == 0) {
                            Color.parseColor("#e6230a")
                        } else {
                            ContextCompat.getColor(itemView.context, R.color.textColor_Medium_Emphasis)
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
                            ContextCompat.getColor(itemView.context, R.color.textColor_Medium_Emphasis)
                        },
                        bold = true
                    )
                else ""

            selectBg.setBackgroundColor(
                if (data?.isSelected == true) ContextCompat.getColor(itemView.context, R.color.textColor_Medium_Emphasis)
                else 0
            )

        }
        itemView.isEnabled = data?.date?.isEmpty() != true
    }

}