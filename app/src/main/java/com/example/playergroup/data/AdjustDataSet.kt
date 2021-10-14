package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst
data class AdjustDataSet(
    val viewType: ViewTypeConst,
    val title: String,
    val subTitle: String,
    var isAdjustMode: Boolean = false
    //todo 화면들이 만들질 경우 랜딩타입 정보를 넣어 줄 예정
)
