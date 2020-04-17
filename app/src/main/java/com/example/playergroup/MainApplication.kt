package com.example.playergroup

import android.app.Application
import com.example.playergroup.data.UserInfo

class MainApplication: Application() {

    companion object {
        @Volatile
        private var instance: MainApplication? = null

        @JvmStatic
        fun getInstance(): MainApplication =
            instance ?: synchronized(this) {
                instance ?: MainApplication().also {
                    instance = it
                }
            }
    }

    /**
     * UserInfo
     */
    private var mUserInfo: UserInfo? = null
    fun setUserInfo(userInfo: UserInfo?) {
        mUserInfo = userInfo
    }
    fun getUserInfo() = mUserInfo
}