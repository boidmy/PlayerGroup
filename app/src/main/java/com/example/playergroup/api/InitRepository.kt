package com.example.playergroup.api

import com.example.playergroup.data.IntroData
import com.example.playergroup.ui.base.BaseRepository

class InitRepository: BaseRepository() {

    /**
     * App Version 정보
     */
    fun getVersionInfo(callback: (IntroData?) -> Unit) {
        firebaseInit.document("versionInfo").get()
            .addOnCompleteListener {
                val introData = (it.result?.toObject(IntroData::class.java))
                callback.invoke(introData)
            }
    }
}