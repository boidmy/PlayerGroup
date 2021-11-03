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

    private const val SEC = 60
    private const val MIN = 60
    private const val HOUR = 24
    private const val DAY = 30
    private const val MONTH = 12

    fun txtDate(tempDate: Date): String {
        val curTime = System.currentTimeMillis()
        val regTime = tempDate.time
        var diffTime = (curTime - regTime) / 1000
        val msg: String?
        when {
            diffTime < SEC -> {
                msg = diffTime.toString() + "초전"
            }
            SEC.let { diffTime /= it; diffTime } < MIN -> {
                msg = diffTime.toString() + "분 전"
            }
            MIN.let { diffTime /= it; diffTime } < HOUR -> {
                msg = diffTime.toString() + "시간 전"
            }
            /*HOUR.let { diffTime /= it; diffTime } < DAY -> {
                msg = diffTime.toString() + "일 전"
            }
            DAY.let { diffTime /= it; diffTime } < MONTH -> {
                msg = diffTime.toString() + "개월 전"
            }
            else -> {
                val sdf = SimpleDateFormat("yyyy")
                val curYear = sdf.format(curTime)
                val passYear = sdf.format(tempDate)
                val diffYear = curYear.toInt() - passYear.toInt()
                msg = diffYear.toString() + "년 전"
            }*/
            else -> msg = getDateFormat(tempDate)
        }
        return msg
    }
}