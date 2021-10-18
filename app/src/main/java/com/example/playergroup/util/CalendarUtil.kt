package com.example.playergroup.util

import java.text.SimpleDateFormat
import java.util.*

object CalendarUtil {

    fun getDate(): Date {
        return Calendar.getInstance().time
    }

    fun getDateFormat(date: Date): String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.run {
            format(date)
        }
    }
}