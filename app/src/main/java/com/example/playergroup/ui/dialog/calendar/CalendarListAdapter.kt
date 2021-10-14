package com.example.playergroup.ui.dialog.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playergroup.ui.dialog.calendar.CalendarViewModel.*
import com.example.playergroup.databinding.ViewCalendarDayBinding
import com.example.playergroup.util.click
import com.example.playergroup.util.diffUtilExtensions

class CalendarListAdapter: RecyclerView.Adapter<CalendarItemViewHolder>() {

    var items: MutableList<CalendarData>? = null
        set(value) {
            value?.let {
                diffUtilExtensions(
                    oldList = field,
                    newList = it,
                    itemCompare = { o, n -> o?.id == n?.id },
                    contentCompare = { o, n -> o == n }
                )
                field?.let {
                    it.clear()
                    it.addAll(value)
                } ?: run {
                    field = value
                }
            }
        }

    private fun select(index: Int) {
        val data = items?.map { it.copy() }?.toMutableList()

        val selectedItem = data?.indexOfFirst { it.isSelected } ?: -1  // true 된 아이템을 찾는다.
        if (selectedItem == -1) {
            data?.getOrNull(index)?.isSelected = true // 없으면 그냥 선택한 아이템 Select
        } else {
            if (selectedItem == index) {    // 같은걸 눌렀을 때는 상태 변경.
                data?.getOrNull(index)?.apply {
                    isSelected = !isSelected
                }
            } else {
                data?.getOrNull(selectedItem)?.isSelected = false   // 같은게 아닐 경우 서로 바꿈.
                data?.getOrNull(index)?.isSelected = true
            }
        }

        items = data
    }

    override fun getItemCount(): Int = items?.size ?: 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemViewHolder =
        CalendarItemViewHolder(ViewCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: CalendarItemViewHolder, position: Int) {
        holder.onBindView(items?.getOrNull(position))
        holder.itemView click {
            select(position)
        }
    }
}