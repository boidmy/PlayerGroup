package com.example.playergroup.ui.club.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class CreateClubViewModel: BaseViewModel() {

    private val _firebaseCreateClubResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseCreateClubResult: LiveData<Boolean>
        get() = _firebaseCreateClubResult

    private val _isVisibleBtnCreateClub: MutableLiveData<Boolean> = MutableLiveData()
    val isVisibleBtnCreateClub: LiveData<Boolean>
        get() = _isVisibleBtnCreateClub

    fun insertInitCreateClub(clubName: String, clubImgUri: Uri?) {
        val isInsertFirebaseClubDB = PublishSubject.create<Boolean>()
        val isInsertStorageClubDB = PublishSubject.create<Boolean>()
        val isUpDateFirebaseUserDB = PublishSubject.create<Boolean>()

        compositeDisposable.add(
            Observable.zip(
                isInsertFirebaseClubDB,
                isInsertStorageClubDB,
                isUpDateFirebaseUserDB,
                { a, b, c -> (a && b && c) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _firebaseCreateClubResult.value = it
                }
        )

        clubRepository.insertInitCreateClub(clubName, clubImgUri) {
            isInsertFirebaseClubDB.onNext(it)
        }
        clubRepository.upLoadClubImg(clubName, clubImgUri) {
            //todo 사진 업로드는 실패해도 일단 패스 하자 ..
            isInsertStorageClubDB.onNext(true)
        }
        authRepository.upDateClubUserField(clubName) {
            isUpDateFirebaseUserDB.onNext(it)
        }
    }

    fun isClubEmptyOverlapCheck(clubName: String, callback: (Boolean) -> Unit) {
        clubRepository.isClubEmpty(clubName) { isSuccessful ->
            callback.invoke(isSuccessful)
        }
    }

    fun onNextObservable(editTextString: String) {
        compositeDisposable.add(
            Observable.create<CharSequence> { emitter -> emitter.onNext(editTextString) }
                .subscribeOn(Schedulers.computation())
                .map { // 최소 1글자 이상 최대 20글자 이하
                    it.length in 2..20
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _isVisibleBtnCreateClub.value = false
                }
        )
    }
}