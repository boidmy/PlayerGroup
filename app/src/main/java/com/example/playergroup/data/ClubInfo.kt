package com.example.playergroup.data

import com.example.playergroup.util.ViewTypeConst
import com.example.playergroup.util.ViewTypeConst.EMPTY_ERROR

data class ClubInfo (
    var clubAdmin: String? = null,
    var clubImg: String? = null,
    var clubName: String? = null,
    var clubCreateDate: String? = null,
    var clubPrimaryKey: String? = null,
    var clubActivityArea: String? = null,
    override var viewType: ViewTypeConst = EMPTY_ERROR
): BaseDataSet(viewType)

data class ClubTabInfo(
    val name: String,
    val primaryKey: String,
    var isNewFeed: Boolean = false,
    var isSelected: Boolean = false,
    val tabType: ViewTypeConst
)