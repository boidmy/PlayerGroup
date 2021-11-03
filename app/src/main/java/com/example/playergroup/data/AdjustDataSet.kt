package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst
data class AdjustDataSet(
    val viewType: ViewTypeConst,
    val title: String,
    val subTitle: String,
    var isAdjustMode: Boolean = false,
    val landingType: Landing = Landing.DONE
)
