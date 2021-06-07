package com.example.playergroup.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

open class RouterEvent (
    val type: Landing,
    var param: String? = null,
    var activityResult: ActivityResultLauncher<Intent>? = null
)

enum class Landing {
    MAIN,
    LOGIN,
    GOOGLE_LOGIN
}