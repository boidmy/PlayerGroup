package com.example.playergroup.data

import com.example.playergroup.util.CategoryUtil

data class NoticeBoardCategoryList(
    val categoryList: MutableList<String> = NoticeInformation.getCategoryList()
)

data class NoticeBoardCategory(
    val categoryKey: String = "",
    val categoryNm: String = ""
)

object NoticeInformation {

    fun getCategoryList(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        list.add(CategoryUtil.FREE_CATEGORY.value)
        list.add(CategoryUtil.RECRUIT_CATEGORY.value)
        return list
    }
}