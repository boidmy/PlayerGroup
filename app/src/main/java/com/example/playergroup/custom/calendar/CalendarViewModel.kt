package com.example.playergroup.custom.calendar

import androidx.lifecycle.MutableLiveData
import com.example.playergroup.custom.calendar.BaseCalendar.Companion.DATE_FORMAT_D
import com.example.playergroup.custom.calendar.BaseCalendar.Companion.DATE_FORMAT_YYYYMM
import com.example.playergroup.custom.calendar.BaseCalendar.Companion.DATE_FORMAT_YYYY_MM
import com.example.playergroup.custom.calendar.BaseCalendar.Companion.DAYS_OF_WEEK
import com.example.playergroup.custom.calendar.CalendarViewModel.*
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.getSimpleFormat
import com.example.playergroup.util.getToday
import java.util.*

typealias GetCalendarDataSet = (() -> MutableList<CalendarData>?)
class CalendarViewModel: BaseViewModel() {

    val calendarListLiveData: MutableLiveData<MutableList<CalendarData>> = MutableLiveData()
    val calendarCurrentDateLiveData: MutableLiveData<String> = MutableLiveData()
    var getCalendarDataSet: GetCalendarDataSet? = null

    private val baseCalendar by lazy { BaseCalendar() }

    fun getSelectedDate(): String {
        val dataSet = getCalendarDataSet?.invoke()
        val selectedItem = dataSet?.indexOfFirst { it.isSelected } ?: -1  // true 된 아이템을 찾는다.
        return if (selectedItem == -1) {
            // 선택 된게 없다.
            ""
        } else {
            // 선택 된게 있다.
            getSimpleFormat(DATE_FORMAT_YYYYMM).format((baseCalendar.calendar as GregorianCalendar).time) + String.format("%02d", dataSet?.getOrNull(selectedItem)?.date?.toInt())
        }
    }

    fun setCalendarData() {
        baseCalendar.initBaseCalendar {
            calendarCurrentDateLiveData.value = getSimpleFormat(DATE_FORMAT_YYYY_MM).format(it.time)
        }
        calendarListLiveData.value = getCalendarDataList()
    }

    fun setAfterMonth() {
        baseCalendar.changeToNextMonth {
            calendarCurrentDateLiveData.value = getSimpleFormat(DATE_FORMAT_YYYY_MM).format(it.time)
        }
        calendarListLiveData.value = getCalendarDataList()
    }

    fun setBeforeMonth() {
        baseCalendar.changeToPrevMonth {
            calendarCurrentDateLiveData.value = getSimpleFormat(DATE_FORMAT_YYYY_MM).format(it.time)
        }
        calendarListLiveData.value = getCalendarDataList()
    }

    fun setAfterYear() {
        baseCalendar.changeToNextYear {
            calendarCurrentDateLiveData.value = getSimpleFormat(DATE_FORMAT_YYYY_MM).format(it.time)
        }
        calendarListLiveData.value = getCalendarDataList()
    }

    fun setBeforeYear() {
        baseCalendar.changeToPrevYear {
            calendarCurrentDateLiveData.value = getSimpleFormat(DATE_FORMAT_YYYY_MM).format(it.time)
        }
        calendarListLiveData.value = getCalendarDataList()
    }

    private fun getCalendarDataList(): MutableList<CalendarData> {
        val calendarDataList = mutableListOf<CalendarData>()
        baseCalendar.data.forEachIndexed { index, date ->

            val day =
                if (index < baseCalendar.prevMonthTailOffset || index >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) "" else date.toString()

            val isTodayYearMonth = getSimpleFormat(DATE_FORMAT_YYYYMM).format((baseCalendar.calendar as GregorianCalendar).time).compareTo(
                getToday(DATE_FORMAT_YYYYMM))

            calendarDataList.add(
                CalendarData(
                    date = day,
                    isToday = if (isTodayYearMonth == 0 && day.isNotEmpty()) getToday(DATE_FORMAT_D) == baseCalendar.data[index].toString() else false,
                    isHoliday = (index % DAYS_OF_WEEK == 0)
                )
            )
        }
        return calendarDataList
    }

    data class CalendarData(
        val id: String = UUID.randomUUID().toString(),   // DiffUtil 에서 비교할 때 필요.
        var date: String? = null,           // 날짜
        var isSelected: Boolean = false,    // 선택 여부
        var isToday: Boolean = false,   // 오늘이라는 텍스트 노출 여부
        var isHoliday: Boolean = false, // 휴일
    )
}