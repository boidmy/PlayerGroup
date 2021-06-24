package com.example.playergroup.util

import android.content.Context
import android.content.SharedPreferences

class ConfigModule(context: Context) {
    private val configPref: SharedPreferences by lazy { PrefUtil.init(context, CONFIG_PREF_NAME) }

    companion object {
        const val CONFIG_PREF_NAME: String = "config_pref"

        const val KEY_THEME_MODE: String = "key_theme_mode"
    }

    var configThemeMode: String?
        get() = PrefUtil.getString(configPref, KEY_THEME_MODE)
        set(value) = PrefUtil.putString(configPref, KEY_THEME_MODE, value)

    /*// 생체 인증 활성화 여부
    var isBiometricEnabled: Boolean?
        get() = PrefUtil.getBoolean(configPref, KEY_BIOMETRIC_LOCK)
        set(value) = PrefUtil.putBoolean(configPref, KEY_BIOMETRIC_LOCK, value)

    // 패턴 정보
    var configPatternLockInfo: String?
        get() = PrefUtil.getString(configPref, KEY_PATTERN_LOCK)
        set(value) = PrefUtil.putString(configPref, KEY_PATTERN_LOCK, value)*/
}