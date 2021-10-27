package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst

data class SearchDataSet(
    override var viewType: ViewTypeConst,
    var data: ClubInfo? = null
): BaseDataSet()
