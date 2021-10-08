package com.example.playergroup.ui.dialog.scrollselector

import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet.Companion.ScrollSelectorType
import com.example.playergroup.ui.dialog.scrollselector.ScrollSelectorBottomSheet.Companion.ScrollSelectorType.*
import com.example.playergroup.ui.base.BaseViewModel
import java.util.*

class ScrollSelectorViewModel: BaseViewModel() {

    private val positionList = mutableListOf<String>(
        "포인트가드 [1]",
        "슈팅가드 [2]",
        "스몰포워드 [3]",
        "파워포워드 [4]",
        "센터 [5]"
    )

    private val weightList = mutableListOf<String>(
        "40",
        "50",
        "60",
        "70",
        "80",
        "90",
        "100",
        "110"
    )

    private val sexList = mutableListOf<String>(
        "남자",
        "여자"
    )

    private fun getYearOfBirth(): MutableList<String> {
        val list = mutableListOf<String>()
        val todayYear = Calendar.getInstance().get(Calendar.YEAR)
        for (i in (todayYear - 60)..todayYear) {
            list.add(i.toString())
        }
        return list
    }

    private fun getHeight(): MutableList<String> {
        val weightList = mutableListOf<String>()
        for (i in 131..210) {
            weightList.add(i.toString())
        }
        return weightList
    }

    fun getSelectorDataList(type: ScrollSelectorType) =
        when(type) {
            WEIGHT -> weightList
            HEIGHT -> getHeight()
            YEAROFBIRTH -> getYearOfBirth()
            SEX -> sexList
            else -> positionList
        }

    fun getSelectorTitle(type: ScrollSelectorType) =
        when(type) {
            WEIGHT -> "몸무게를 선택해주세요."
            HEIGHT -> "키를 선택해주세요."
            YEAROFBIRTH -> "출생연도를 선택해주세요."
            SEX -> "성별을 선택해주세요."
            else -> "포지션을 선택해주세요."
        }
}