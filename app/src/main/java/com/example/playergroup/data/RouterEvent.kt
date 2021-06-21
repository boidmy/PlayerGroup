package com.example.playergroup.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

const val INTENT_EXTRA_PARAM = "intent_extra_param"

open class RouterEvent (
    val type: Landing,
    var paramTxt: String? = null,
    var paramBoolean: Boolean = false,
    var activityResult: ActivityResultLauncher<Intent>? = null
)

enum class Landing {
    MAIN,
    LOGIN,
    GOOGLE_LOGIN,
    GALLERY,
    MY_PAGE
}