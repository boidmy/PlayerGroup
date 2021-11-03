package com.example.playergroup

import android.app.Application
import android.util.Log
import com.example.playergroup.data.UserInfo
import com.example.playergroup.util.ConfigModule
import com.example.playergroup.util.applyTheme
import com.example.playergroup.util.getThemeMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PlayerGroupApplication: Application() {

    private lateinit var userInfoSnapShort: ListenerRegistration    //todo 언제 제거해줘야 할까 ?
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

    /**
     * User 정보는 항상 최신으로 유지해야 한다 .
     */
    private fun userInfoSnapShort() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email.isNullOrEmpty()) return
        userInfoSnapShort = FirebaseFirestore.getInstance().collection("users").document(email).addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("####", "userInfoSnapShort Listen failed. ${error.message}")
                return@addSnapshotListener
            }

            if (value != null && value.exists()) {
                Log.d("####", "userInfoSnapShort : " + value.data.toString())
                val userInfo = value.toObject(UserInfo::class.java)
                this.userInfo = userInfo
            } else {
                Log.d("####", "userInfoSnapShort : null")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        applyTheme(getThemeMode(configModule.configThemeMode))
        userInfoSnapShort()
    }
}