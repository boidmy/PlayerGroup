package com.example.playergroup.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playergroup.data.AppUpDateType
import com.example.playergroup.data.AppUpDateType.NONE
import com.example.playergroup.data.Category
import com.example.playergroup.data.UserInfo
import com.example.playergroup.ui.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class IntroViewModel: BaseViewModel() {

    private val _firebaseAppVersionInfoResult: MutableLiveData<Pair<AppUpDateType, String>> = MutableLiveData()
    val firebaseAppVersionInfoResult: LiveData<Pair<AppUpDateType, String>>
        get() = _firebaseAppVersionInfoResult
    private val _boardCategoryList: MutableLiveData<MutableList<Category>> = MutableLiveData()

    private val _firebaseUserDataResult: MutableLiveData<UserInfo?> = MutableLiveData()
    val firebaseUserDataResult: LiveData<UserInfo?>
        get() = _firebaseUserDataResult
    val boardCategoryList: LiveData<MutableList<Category>>
        get() = _boardCategoryList

    fun getAppVersionInfo(appNowVersion: String) {
        initRepository.getVersionInfo {
            val appNewVersion = it?.appNewVersion
            val type = if (!appNewVersion.isNullOrEmpty()) {
                    if (isAppVersionNew(appNewVersion, appNowVersion)) it.getUpDateType()
                    else NONE
                } else NONE
            val upDateInfo = it?.appUpdateInfo ?: "새로운 버전이 출시되었습니다."
            _firebaseAppVersionInfoResult.value = Pair(type, upDateInfo)
            _boardCategoryList.value = it?.category
        }
    }

    val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    fun getUserProfile(userEmail: String?) {
        authRepository.getUserProfileData(userEmail) {
            _firebaseUserDataResult.value = it
        }
    }

    /**
     * 버전 비교
     */
    private fun isAppVersionNew(serverVersion: String, appVersion: String): Boolean {
        if (serverVersion.isBlank()) return false

        val serverVersions = serverVersion.split('.')
        val localVersions = appVersion.split('.')
        for (i in 0..serverVersions.count()) {
            if (i < localVersions.count()) {
                if (serverVersions[i] > localVersions[i]) return true else continue
            }
        }
        return false
    }

}