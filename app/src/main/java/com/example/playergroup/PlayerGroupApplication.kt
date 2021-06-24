package com.example.playergroup

import android.app.Application
import com.example.playergroup.util.ConfigModule
import com.example.playergroup.util.applyTheme
import com.example.playergroup.util.getThemeMode

class PlayerGroupApplication: Application() {
    companion object {
        lateinit var _instance: PlayerGroupApplication
        fun getInstance() = _instance
    }

    init {
        _instance = this
    }

    private val configModule by lazy { ConfigModule(this) }

    override fun onCreate() {
        super.onCreate()
        applyTheme(getThemeMode(configModule.configThemeMode))
    }
}