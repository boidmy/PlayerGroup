package com.example.playergroup.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.BaseDataSet
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst.SEARCH_ONE_TYPE
import com.example.playergroup.util.ViewTypeConst.SEARCH_TWO_TYPE
import com.example.playergroup.util.checkItemsAre

class SearchViewModel: BaseViewModel() {

    private val _firebaseClubListData: MutableLiveData<List<ClubInfo>?> = MutableLiveData()
    val firebaseClubListData: LiveData<List<ClubInfo>?>
        get() = _firebaseClubListData

    fun List<BaseDataSet>?.getClubAllList(isTwoItemMode: Boolean) {
        if (this.isNullOrEmpty()) {
            clubRepository.getClubList {
                _firebaseClubListData.value = it modeChange isTwoItemMode
            }
        } else {
            val data = this.toMutableList().checkItemsAre<ClubInfo>()?.toList()
            _firebaseClubListData.value = data modeChange isTwoItemMode
        }
    }

    private infix fun List<ClubInfo>?.modeChange(isTwoItemMode: Boolean): List<ClubInfo>? =
        this?.map { it.copy() }?.run {
            map { it.viewType = if (isTwoItemMode) SEARCH_TWO_TYPE else SEARCH_ONE_TYPE }
            this
        }
}