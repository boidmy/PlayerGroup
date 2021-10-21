package com.example.playergroup.ui.mypage

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MyPageViewModel: BaseViewModel() {

    var profileImgUri: Uri? = null

    val isEditModeEvent = PublishSubject.create<Boolean>()

    private val _firebaseResult: MutableLiveData<Boolean> = MutableLiveData()
    val firebaseResult: LiveData<Boolean>
        get() = _firebaseResult

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun saveProfile(userInfo: UserInfo) {
        // 사진을 먼저 저장 후 Full Url 을 갖고 와야 한다.
        authRepository.upLoadStorageImg(profileImgUri!!) { isStorageUpLoadState ->   // Storage 에 우선 저장
            if (isStorageUpLoadState) {
                authRepository.getUserProfilePhoto(userInfo.email) { storageFullUrl -> // Storage 에 저장된 Img Full Url을 갖고 온다.
                    userInfo.img = storageFullUrl

                    authRepository.insertInitUserDocument(userInfo) {   // user DB 에 userInfo 를 저장한다.
                        _firebaseResult.value = it
                    }
                }
            } else {
                _firebaseResult.value = false
            }
        }
    }
}