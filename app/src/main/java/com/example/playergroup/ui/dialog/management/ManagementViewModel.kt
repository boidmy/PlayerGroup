package com.example.playergroup.ui.dialog.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.Landing
import com.example.playergroup.data.ManagementDataSet
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class ManagementViewModel: BaseViewModel() {

    private val _makeClubLiveData: MutableLiveData<MutableList<ManagementDataSet>?> = MutableLiveData()
    val makeClubLiveData: LiveData<MutableList<ManagementDataSet>?>
        get() = _makeClubLiveData

    private val _involvedClubLiveData: MutableLiveData<MutableList<ManagementDataSet>?> = MutableLiveData()
    val involvedClubLiveData: LiveData<MutableList<ManagementDataSet>?>
        get() = _involvedClubLiveData

    fun getClubList(makeClubList: MutableList<String>?, involvedClubList: MutableList<String>?) {
        val makeClubModule = mutableListOf<ManagementDataSet>()
        if (makeClubList.isNullOrEmpty()) {
            makeClubModule.add(ManagementDataSet(
                viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                emptyTxt = "새로은 클럽을 만들어보세요!",
                emptyLandingType = Landing.CREATE_CLUB
            ))
        } else {
            clubRepository.getClubList(makeClubList.toList()) {
                val sortList = it?.sortedBy { it.clubCreateDate }
                sortList?.forEachIndexed { index, clubInfo ->
                    makeClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_ITEM,
                        clubImg = clubInfo.clubImg,
                        clubName = clubInfo.clubName,
                        clubPrimaryKey = clubInfo.clubPrimaryKey
                    ))
                }
                if (makeClubModule.size == 0) {
                    makeClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                        emptyTxt = "새로은 클럽을 만들어보세요!",
                        emptyLandingType = Landing.CREATE_CLUB
                    ))
                }
            }
        }
        _makeClubLiveData.value = makeClubModule

        val involvedClubModule = mutableListOf<ManagementDataSet>()

        if (involvedClubList.isNullOrEmpty()) {
            involvedClubModule.add(ManagementDataSet(
                viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                emptyTxt = "클럽에 가입해 보세요!",
                emptyLandingType = Landing.SEARCH
            ))
        } else {
            clubRepository.getClubList(involvedClubList.toList()) {
                val sortList = it?.sortedBy { it.clubCreateDate }
                sortList?.forEachIndexed { index, clubInfo ->
                    involvedClubModule.add(
                        ManagementDataSet(
                            viewType = ViewTypeConst.MANAGEMENT_ITEM,
                            clubImg = clubInfo.clubImg,
                            clubName = clubInfo.clubName,
                            clubPrimaryKey = clubInfo.clubPrimaryKey
                        )
                    )
                }
            }
        }
        _involvedClubLiveData.value = involvedClubModule
    }

}