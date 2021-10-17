package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst

data class MainDataSet(
    override var viewType: ViewTypeConst,
    var data: Any? = null
): BaseDataSet()
