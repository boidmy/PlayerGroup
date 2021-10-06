package com.example.playergroup.data

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat

const val INTENT_EXTRA_PARAM = "intent_extra_param"
const val INTENT_EXTRA_PARAM1 = "intent_extra_param1"

const val INTENT_EXTRA_PRIMARY_KEY = "intent_extra_primary_key"
const val INTENT_EXTRA_STRING_PARAM = "intent_extra_string_param"
const val INTENT_EXTRA_URI_TO_STRING_PARAM = "intent_extra_uri_to_string_param"

open class RouterEvent (
    val type: Landing,
    var primaryKey: String? = null,
    var paramString: String? = null,
    var paramUriToString: String? = null,
    var paramBoolean: Boolean = false,
    var paramInt: Int = -1,
    var activityResult: ActivityResultLauncher<Intent>? = null,
    var options: ActivityOptions? = null
)

enum class Landing {
    MAIN,
    START_LOGIN_SCREEN,
    LOGIN,
    LOGOUT,
    DROP_OUT,
    GOOGLE_LOGIN,
    GALLERY,
    MY_PAGE,
    CREATE_CLUB,
    CLUB_MAIN,
    SEARCH,
    SETTING,
    THEME_SELECTOR,
    APP_PERMISSION_SETTING
}