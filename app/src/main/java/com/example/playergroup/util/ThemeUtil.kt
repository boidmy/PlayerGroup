package com.example.playergroup.util

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

enum class ThemeMode(val value: String) {
    LIGHT_MODE("lightMode"),
    DARK_MODE("darkMode"),
    DEFAULT_MODE("defaultMode")
}

fun getThemeMode(themeMode: String?) = when (themeMode) {
    ThemeMode.LIGHT_MODE.value -> ThemeMode.LIGHT_MODE
    ThemeMode.DARK_MODE.value -> ThemeMode.DARK_MODE
    else -> ThemeMode.DEFAULT_MODE
}

fun applyTheme(themeColor: ThemeMode) = when(themeColor) {
    ThemeMode.LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    ThemeMode.DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    ThemeMode.DEFAULT_MODE -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) // 안드로이드 10 이상
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        else // 안드로이드 10 미만
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
    }
}
