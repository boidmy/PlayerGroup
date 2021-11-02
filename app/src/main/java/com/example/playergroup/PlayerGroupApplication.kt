package com.example.playergroup

import android.app.Application
import com.example.playergroup.data.UserInfo
import com.example.playergroup.util.ConfigModule
import com.example.playergroup.util.applyTheme
import com.example.playergroup.util.getThemeMode
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PlayerGroupApplication: Application() {
    companion object {
        lateinit var instance: PlayerGroupApplication
    }

    init {
        instance = this
    }

    private val configModule by lazy { ConfigModule(this) }

    /**
     * GlobalData
     */
    var userInfo: UserInfo? = null
    fun isLogin() = FirebaseAuth.getInstance().currentUser != null
    fun setLogout() {
        userInfo = null
    }

    override fun onCreate() {
        super.onCreate()
        applyTheme(getThemeMode(configModule.configThemeMode))
    }
}