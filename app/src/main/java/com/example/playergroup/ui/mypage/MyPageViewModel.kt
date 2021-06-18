package com.example.playergroup.ui.mypage

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.UserInfo
import com.example.playergroup.data.repository.AuthRepository
import com.example.playergroup.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MyPageViewModel: BaseViewModel() {

    var profileImgUri: Uri? = null

    private val isImageUpLoad = PublishSubject.create<Boolean>()
    private val isUserInfoUpLoad = PublishSubject.create<Boolean>()

    private val _firebaseResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseResult: LiveData<Boolean>
        get() = _firebaseResult

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun saveProfile(userInfo: UserInfo) {
        compositeDisposable.add(
            Observable.zip(
                isImageUpLoad,
                isUserInfoUpLoad,
                BiFunction{ imgUpLoad: Boolean, userUpload: Boolean -> (imgUpLoad && userUpload)})
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _firebaseResult.value = it
                }, {
                    _firebaseResult.value = false
                })
        )
        setUpLoadProfile(userInfo)
    }

    private fun setUpLoadProfile(userInfo: UserInfo) {
        if (profileImgUri != null) {
            authRepository.upLoadStorageImg(profileImgUri!!) {
                isImageUpLoad.onNext(it)
            }
        }
        authRepository.insertUserDocument(userInfo) {
            isUserInfoUpLoad.onNext(it)
        }
    }

    fun setCurrentUserProfile(url: String) {
        authRepository.insertUserProfilePhoto(url) {

        }
    }

}