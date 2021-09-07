package com.example.playergroup.ui.mypage

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MyPageViewModel: BaseViewModel() {

    var profileImgUri: Uri? = null

    val isEditModeEvent = PublishSubject.create<Boolean>()

    private val isImageUpLoad = PublishSubject.create<Boolean>()
    private val isUserInfoUpLoad = PublishSubject.create<Boolean>()

    private val _firebaseResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseResult: LiveData<Boolean>
        get() = _firebaseResult

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun getUserProfileImg(userEmail: String?, callback: (String?) -> Unit) {
        authRepository.getUserProfilePhoto(userEmail) {
            callback.invoke(it)
        }
    }

    fun saveProfile(userInfo: UserInfo) {
        compositeDisposable.add(
            Observable.zip(
                isImageUpLoad,
                isUserInfoUpLoad,
                BiFunction{ imgUpLoad: Boolean, userUpload: Boolean -> (imgUpLoad && userUpload)})
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _firebaseResult.value = it
                    //todo 다른곳에서 저장을 하는게 좋을거 같은데 .. 구조를 바꿔야 한다 ........
                    Log.d("####", "userInfo >> "+userInfo.email)
                    if (it) PlayerGroupApplication.instance.userInfo = userInfo
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
        } else {
            isImageUpLoad.onNext(true)  // 이미지 저장 안해도 패스..
        }
        authRepository.insertInitUserDocument(userInfo) {
            isUserInfoUpLoad.onNext(it)
        }
    }

    fun getNameOverlapCheck(field: String) {
        authRepository.getOverlapCheck(field) { isEmpty ->
        }
    }

}