package com.example.playergroup.data

import java.io.Serializable
import java.util.*

data class NoticeBoardList(
    val items: List<NoticeBoardItem> = listOf()
)

data class NoticeBoardItem(
    var title: String = "",
    var sub: String = "",
    val email: String = "",
    var key: String = "",
    var time: Date = Date(),
    val name: String = "",
    var categoryKey: String = ""
): Serializable