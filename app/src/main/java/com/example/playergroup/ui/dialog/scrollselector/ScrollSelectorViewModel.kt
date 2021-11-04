package com.example.playergroup.ui.dialog.scrollselector

import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.NoticeBoardCategory
import com.example.playergroup.data.NoticeBoardCategoryList
import com.example.playergroup.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.ViewTypeConst.*
import java.util.*

class ScrollSelectorViewModel : BaseViewModel() {

    private val positionList = mutableListOf<String>("포인트가드 [1]", "슈팅가드 [2]", "스몰포워드 [3]", "파워포워드 [4]", "센터 [5]")
    private val weightList = mutableListOf<String>("40", "50", "60", "70", "80", "90", "100", "110")
    private val sexList = mutableListOf<String>("남자", "여자")
    private val cityList = mutableListOf<String>(
        "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도" ,"강원도",
        "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"
    )

    private var boardCategoryList: MutableList<String> = PlayerGroupApplication.instance.boardCategoryList?.map {
        it.categoryNm
    } as MutableList<String>

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

    fun getSelectorDataList(type: ViewTypeConst) =
        when(type) {
            SCROLLER_WEIGHT -> weightList
            SCROLLER_HEIGHT -> getHeight()
            SCROLLER_YEAROFBIRTH -> getYearOfBirth()
            SCROLLER_SEX -> sexList
            SCROLLER_ACTIVITY_AREA -> cityList
            SCROLLER_CATEGORY -> boardCategoryList
            else -> positionList
        }

    fun getSelectorTitle(type: ViewTypeConst) =
        when(type) {
            SCROLLER_WEIGHT -> "몸무게를 선택해주세요."
            SCROLLER_HEIGHT -> "키를 선택해주세요."
            SCROLLER_YEAROFBIRTH -> "출생연도를 선택해주세요."
            SCROLLER_SEX -> "성별을 선택해주세요."
            SCROLLER_ACTIVITY_AREA -> "활동지역을 선택해주세요."
            SCROLLER_CATEGORY -> "카테고리를 선택해주세요."
            else -> "포지션을 선택해주세요."
        }
}