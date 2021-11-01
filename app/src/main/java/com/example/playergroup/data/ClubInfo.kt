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
    override var viewType: ViewTypeConst = EMPTY_ERROR,
    var joinProgress: MutableList<String>? = null,   // 가입 절차 진행중 ( 프라이머리 키 )
    var member: MutableList<String>? = null,    // 클럽 회원 ( 프라이머리 키 )
): BaseDataSet(viewType)

data class ClubTabInfo(
    val name: String,
    val primaryKey: String,
    var isNewFeed: Boolean = false,
    var isSelected: Boolean = false,
    val tabType: ViewTypeConst
)

data class ClubMemberDataSet(
    override var viewType: ViewTypeConst,
    var isExpanded: Boolean = false,
    val isJoiningUser: Boolean = false,
    val name: String = "",
    val img: String = "",
    val email: String = "",
    val titleText: String = "",
    val playPosition: String = ""
): BaseDataSet()