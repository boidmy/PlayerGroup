package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst
data class AdjustDataSet(
    override var viewType: ViewTypeConst,
    val title: String,
    val subTitle: String
): BaseDataSet()
