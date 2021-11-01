package com.example.playergroup.ui.club

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.ClubInfo
import com.example.playergroup.data.ClubMemberDataSet
import com.example.playergroup.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class ClubViewModel: BaseViewModel() {

    private val _firebaseClubDataResult: MutableLiveData<ClubInfo?> = MutableLiveData()
    val firebaseClubDataResult: LiveData<ClubInfo?>
        get() = _firebaseClubDataResult

    private val _clubMemberLiveData: MutableLiveData<MutableList<ClubMemberDataSet>?> = MutableLiveData()
    val clubMemberLiveData: LiveData<MutableList<ClubMemberDataSet>?>
        get() = _clubMemberLiveData

    var joinEvent = PublishSubject.create<Boolean>()
    var joinCancelEvent = PublishSubject.create<Boolean>()

    fun getClubData(primaryKey: String) {
        clubRepository.getClubData(primaryKey) {
            _firebaseClubDataResult.value = it
        }
    }

    fun setJoin(primaryKey: String, email: String) {
        clubRepository.addClubJoinProgress(primaryKey, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                joinEvent.onNext(true)
            }, {
                Log.d("####", "Club Join Error ${it.message}")
                joinEvent.onNext(false)
            })
            .addTo(compositeDisposable)
    }

    fun setJoinCancel(primaryKey: String, email: String) {
        clubRepository.removeClubJoinProgress(primaryKey, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                joinCancelEvent.onNext(true)
            }, {
                Log.d("####", "Club Join Cancel Error ${it.message}")
                joinCancelEvent.onNext(false)
            })
            .addTo(compositeDisposable)
    }

}
