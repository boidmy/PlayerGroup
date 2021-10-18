package com.example.playergroup.data

import java.io.Serializable
import java.util.*

data class NoticeBoardList(
    val items: List<NoticeBoardItem> = listOf()
)

data class NoticeBoardItem(
    val title: String = "",
    val sub: String = "",
    val email: String = "",
    var key: String = "",
    var time: Date = Date(),
    val name: String = ""
): Serializable