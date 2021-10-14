package com.example.playergroup.util

import android.content.Context
import android.content.SharedPreferences

class ConfigModule(context: Context) {
    private val configPref: SharedPreferences by lazy { PrefUtil.init(context, CONFIG_PREF_NAME) }

    companion object {
        const val CONFIG_PREF_NAME: String = "config_pref"

        const val KEY_THEME_MODE: String = "key_theme_mode"
        const val SEARCH_LIST_TWO_TYPE_MODE: String = "search_list_two_type_mode"
        const val ADJUST_MAIN_MENU_LIST: String = "adjust_main_menu_list"
    }

    var configThemeMode: String?
        get() = PrefUtil.getString(configPref, KEY_THEME_MODE)
        set(value) = PrefUtil.putString(configPref, KEY_THEME_MODE, value)

    var isTwoItemMode: Boolean?
        get() = PrefUtil.getBoolean(configPref, SEARCH_LIST_TWO_TYPE_MODE)
        set(value) = PrefUtil.putBoolean(configPref, SEARCH_LIST_TWO_TYPE_MODE, value)

    var adjustMainMenuList: String?
        get() = PrefUtil.getString(configPref, ADJUST_MAIN_MENU_LIST)
        set(value) = PrefUtil.putString(configPref, ADJUST_MAIN_MENU_LIST, value)

    /*// 생체 인증 활성화 여부
    var isBiometricEnabled: Boolean?
        get() = PrefUtil.getBoolean(configPref, KEY_BIOMETRIC_LOCK)
        set(value) = PrefUtil.putBoolean(configPref, KEY_BIOMETRIC_LOCK, value)

    // 패턴 정보
    var configPatternLockInfo: String?
        get() = PrefUtil.getString(configPref, KEY_PATTERN_LOCK)
        set(value) = PrefUtil.putString(configPref, KEY_PATTERN_LOCK, value)*/
}