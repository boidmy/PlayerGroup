package com.example.playergroup.ui.dialog.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.ManagementDataSet
import com.example.playergroup.ui.base.BaseViewModel
import com.example.playergroup.util.ViewTypeConst
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class ManagementViewModel: BaseViewModel() {

    private val _managementLiveData: MutableLiveData<Pair<MutableList<ManagementDataSet>?, MutableList<ManagementDataSet>?>> = MutableLiveData()
    val managementLiveData: LiveData<Pair<MutableList<ManagementDataSet>?, MutableList<ManagementDataSet>?>>
        get() = _managementLiveData

    fun getClubList(makeClubList: MutableList<String>?, involvedClubList: MutableList<String>?) {
        // todo club 정보 갖고 와서 ManagementDataSet 만들어 주기

        val publishMakeClub = PublishSubject.create<MutableList<ManagementDataSet>>()
        val publishInvolvedClub = PublishSubject.create<MutableList<ManagementDataSet>>()

        Observable.zip(publishMakeClub, publishInvolvedClub, {a, b ->
            Pair(a, b)
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _managementLiveData.value = it
            }, {
                _managementLiveData.value = null
            }).addTo(compositeDisposable)


        if (makeClubList.isNullOrEmpty()) {
            publishMakeClub.onNext(mutableListOf(ManagementDataSet(
                viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                emptyTxt = "새로은 클럽을 만들어보세요!"
            )))
        } else {
            clubRepository.getClubList(makeClubList.toList()) {
                val modules = mutableListOf<ManagementDataSet>()
                it?.forEachIndexed { index, clubInfo ->
                    modules.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_ITEM,
                        clubImg = clubInfo.clubImg,
                        clubName = clubInfo.clubName,
                        clubPrimaryKey = makeClubList[index]
                    ))
                }
                publishMakeClub.onNext(modules)
            }
        }

        if (involvedClubList.isNullOrEmpty()) {
            publishInvolvedClub.onNext(mutableListOf(ManagementDataSet(
                viewType = ViewTypeConst.MANAGEMENT_EMPTY,
                emptyTxt = "클럽에 가입해 보세요!"
            )))
        } else {
            clubRepository.getClubList(involvedClubList.toList()) {
                val modules = mutableListOf<ManagementDataSet>()
                it?.forEachIndexed { index, clubInfo ->
                    modules.add(ManagementDataSet(
                        viewType = ViewTypeConst.MANAGEMENT_ITEM,
                        clubImg = clubInfo.clubImg,
                        clubName = clubInfo.clubName,
                        clubPrimaryKey = involvedClubList[index]
                    ))
                }
                publishInvolvedClub.onNext(modules)
            }
        }
    }

}