package com.example.playergroup.data

data class NoticeBoardList(
    val items: List<NoticeBoardItem> = listOf()
)

data class NoticeBoardItem(
    val title: String = "",
    val sub: String = "",
    val userId: String = "",
    val key: String = "",
    val time: String = "",
    val review: MutableList<NoticeBoardItem> = mutableListOf()
)