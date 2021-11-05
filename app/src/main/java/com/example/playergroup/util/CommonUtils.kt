package com.example.playergroup.util

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import com.example.playergroup.BuildConfig
import com.example.playergroup.R
import com.example.playergroup.custom.DialogCustom
import io.reactivex.functions.Function
import java.net.URLEncoder

infix fun Context.debugToast(message: () -> String) {
    if (BuildConfig.DEBUG) Toast.makeText(this, message(), Toast.LENGTH_SHORT).show()
}

inline fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

/**
 * App version
 */
val Context.appVersion: String
    get() {
        var versionName = ""
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            versionName = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return versionName
    }

infix fun Context.showDefDialog(msg: String): DialogCustom =
    DialogCustom(this)
        .setMessage(msg)
        .setConfirmBtnText(R.string.ok)
        .setDialogCancelable(false)
        .setConfirmClickListener(object: DialogCustom.DialogCustomClickListener {
            override fun onClick(dialogCustom: DialogCustom) {
                dialogCustom.dismiss()
            }
        })

fun getFirebaseExceptionCodeToString(errorCode: String) = when(errorCode) {
    "ERROR_INVALID_EMAIL" -> "이메일 주소 형식이 잘못되었습니다."
    "ERROR_WRONG_PASSWORD" -> "비밀번호가 틀렸습니다. 반복될 경우 관리자에게 문의 부탁 드립니다."
    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "이메일 주소는 같지만 로그인 자격 증명이 다른 계정이 이미 있습니다. 이 이메일 주소와 연결된 제공 업체를 사용하여 로그인하십시오."
    "ERROR_EMAIL_ALREADY_IN_USE" -> "이 이메일 주소는 다른 계정에서 사용중 입니다."
    "ERROR_USER_DISABLED" -> "관리자에 의하여 사용자 계정을 비활성화 했습니다. 관리자에게 문의 해 주세요."
    "ERROR_USER_NOT_FOUND" -> "사용자를 찾을 수 없습니다. 확인 후 다시 이용해주세요. 반복될 경우 관리자에게 문의 부탁 드립니다."
    "ERROR_WEAK_PASSWORD" -> "암호가 유효하지 않습니다. 다시 입력해주세요."
    "ERROR_EMAIL_NOT_VERIFICATION" -> "이메일 인증이 되어 있지 않은 계정입니다. 인증 후 다시 시도해 주세요."
    else -> "관리자에게 문의해주세요."
}



@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> MutableList<*>.checkItemsAre() =
    if (all { it is T })
        this as MutableList<T>
    else null

fun String?.urlEncoded(): String {
    return URLEncoder.encode(this, "utf-8")
}

fun getZipper(): Function<Array<Any>, MutableList<Any>> {
    return Function { objects ->
        val resultList = mutableListOf<Any>()
        for (ob in objects) {
            resultList.add(ob)
        }
        resultList
    }
}

fun <T, U, R> Pair<T?, U?>.two(body: (T, U) -> R): R? {
    val first = first
    val second = second
    if (first != null && second != null) {
        return body(first, second)
    }
    return null
}

