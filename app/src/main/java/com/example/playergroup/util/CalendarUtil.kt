package com.example.playergroup.util

import java.text.SimpleDateFormat
import java.util.*

object CalendarUtil {

    fun getDate(): String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }.run {
            format(Calendar.getInstance().time)
        }
    }
}