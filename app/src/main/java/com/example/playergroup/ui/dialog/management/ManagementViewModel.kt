package com.example.playergroup.ui.dialog.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.Landing
import com.example.playergroup.data.ManagementDataSet
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst

class ManagementViewModel: BaseViewModel() {

    private val _makeClubLiveData: MutableLiveData<MutableList<ManagementDataSet>?> = MutableLiveData()
    val makeClubLiveData: LiveData<MutableList<ManagementDataSet>?>
        get() = _makeClubLiveData

    private val _involvedClubLiveData: MutableLiveData<MutableList<ManagementDataSet>?> = MutableLiveData()
    val involvedClubLiveData: LiveData<MutableList<ManagementDataSet>?>
        get() = _involvedClubLiveData

    fun getClubList(
        makeClubList: MutableList<String>?,
        involvedClubList: MutableList<String>?,
        joinProgress: MutableList<String>?
    ) {
        val makeClubModule = mutableListOf<ManagementDataSet>()

        clubRepository.getClubList(makeClubList) {
            if (it.isNullOrEmpty()) {
                makeClubModule.add(ManagementDataSet(
                    viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                    emptyTxt = "새로은 클럽을 만들어보세요!",
                    landing = Landing.CREATE_CLUB
                ))
            } else {
                val sortList = it.sortedBy { it.clubCreateDate }
                sortList.forEachIndexed { index, clubInfo ->
                    makeClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_ITEM,
                        clubImg = clubInfo.clubImg,
                        clubName = clubInfo.clubName,
                        clubPrimaryKey = clubInfo.clubPrimaryKey,
                        landing = Landing.CLUB_MAIN
                    ))
                }
                if (makeClubModule.isNullOrEmpty()) {
                    makeClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                        emptyTxt = "새로은 클럽을 만들어보세요!",
                        landing = Landing.CREATE_CLUB
                    ))
                } else {
                    makeClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_ADD,
                        landing = Landing.CREATE_CLUB
                    ))
                }
            }
            _makeClubLiveData.value = makeClubModule
        }

        val involvedClubModule = mutableListOf<ManagementDataSet>()

        clubRepository.getClubList(involvedClubList) {

            if (!it.isNullOrEmpty()) {
                val sortList = it.sortedBy { it.clubCreateDate }
                sortList.forEachIndexed { index, clubInfo ->
                    involvedClubModule.add(
                        ManagementDataSet(
                            viewType = ViewTypeConst.MANAGEMENT_ITEM,
                            clubImg = clubInfo.clubImg,
                            clubName = clubInfo.clubName,
                            clubPrimaryKey = clubInfo.clubPrimaryKey,
                            landing = Landing.CLUB_MAIN
                        )
                    )
                }
            }

            clubRepository.getClubList(joinProgress) {
                if (!it.isNullOrEmpty()) {
                    it.forEach {
                        involvedClubModule.add(
                            ManagementDataSet(
                                viewType = ViewTypeConst.MANAGEMENT_ITEM,
                                clubImg = it.clubImg,
                                clubName = it.clubName,
                                clubPrimaryKey = it.clubPrimaryKey,
                                isJoinProgress = true,
                                landing = Landing.CLUB_MAIN
                            )
                        )
                    }

                }

                if (involvedClubList.isNullOrEmpty()) {
                    involvedClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                        emptyTxt = "클럽에 가입해 보세요!",
                        landing = Landing.SEARCH
                    ))
                } else {
                    involvedClubModule.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_ADD,
                        landing = Landing.SEARCH
                    ))
                }

                _involvedClubLiveData.value = involvedClubModule
            }
        }

    }
}