package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst

data class ManagementDataSet (
    override var viewType: ViewTypeConst,
    var clubImg: String? = null,
    var clubName: String? = null,
    var emptyTxt: String? = null,
    var clubPrimaryKey: String? = null,
    var emptyLandingType: Landing? = null
): BaseDataSet()