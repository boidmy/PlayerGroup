package com.example.playergroup.ui.club.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.ui.base.BaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class CreateClubViewModel: BaseViewModel() {

    private val _firebaseCreateClubResult: MutableLiveData<Pair<Boolean, String>> = MutableLiveData()
    val firebaseCreateClubResult: LiveData<Pair<Boolean, String>>
        get() = _firebaseCreateClubResult

    private val _isVisibleBtnCreateClub: MutableLiveData<Boolean> = MutableLiveData()
    val isVisibleBtnCreateClub: LiveData<Boolean>
        get() = _isVisibleBtnCreateClub

    fun insertInitCreateClub(clubName: String, clubImgUri: Uri?) {
        clubRepository.upLoadClubImg(clubName, clubImgUri) { isStorageUpLoadState ->   // Storage 에 우선 저장
            if (isStorageUpLoadState) {
                clubRepository.getUserProfilePhoto(clubName) { storageFullUrl -> // Storage 에 저장된 Img Full Url을 갖고 온다.
                    val primaryKey = authRepository.createGetPrimaryKey()
                    //todo storageFullUrl... 사진 업로드는 실패해도 일단 패스 하자 ..
                    clubRepository.insertInitCreateClub(primaryKey, clubName, storageFullUrl ?: "") {
                        if (it) {
                            authRepository.upDateClubUserField(primaryKey) {
                                _firebaseCreateClubResult.value = Pair(it, primaryKey)
                            }
                        } else {
                            _firebaseCreateClubResult.value = Pair(false, "")
                        }
                    }
                }
            } else {
                _firebaseCreateClubResult.value = Pair(false, "")
            }
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