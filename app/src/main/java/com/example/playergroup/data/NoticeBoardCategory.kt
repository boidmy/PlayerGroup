package com.example.playergroup.data

import com.example.playergroup.util.CategoryUtil

data class NoticeBoardCategoryList(
    val categoryList: List<Pair<String, String>> = NoticeInformation.getCategoryList()
)

data class NoticeBoardCategory(
    val categoryKey: String = "",
    val categoryNm: String = ""
)

object NoticeInformation {

    fun getCategoryList(): List<Pair<String, String>> {
        val list: MutableList<Pair<String, String>> = mutableListOf()
        list.add(Pair(CategoryUtil.FREE_CATEGORY.value, CategoryUtil.FREE_CATEGORY.key))
        list.add(Pair(CategoryUtil.RECRUIT_CATEGORY.value, CategoryUtil.RECRUIT_CATEGORY.key))
        return list
    }
}