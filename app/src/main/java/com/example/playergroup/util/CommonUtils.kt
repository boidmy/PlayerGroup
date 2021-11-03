package com.example.playergroup.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentActivity
import com.example.playergroup.BuildConfig
import com.example.playergroup.R
import com.example.playergroup.custom.DialogCustom
import io.reactivex.Observable
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.math.roundToInt
import io.reactivex.functions.Function

private val PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{8,15}.\$")   // 8 ~ 16 ( 특수문자, 문자, 숫자 모두 포함 )

infix fun Context.debugToast(message: () -> String) {
    if (BuildConfig.DEBUG) Toast.makeText(this, message(), Toast.LENGTH_SHORT).show()
}

inline fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, duration).show()

infix fun View?.click(block: (View) -> Unit) = this?.setOnClickListener(block)
infix fun View?.longClick(block: (View) -> Boolean) = this?.setOnLongClickListener(block)
infix fun TextView?.setUnderLineBold(input: Pair<String, String>): Spannable {
    val sb = SpannableStringBuilder(input.first)
    val pair = getChangedIndex(input.first, input.second)
    sb.setSpan(UnderlineSpan(), pair.first, pair.second, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.setSpan(
        StyleSpan(Typeface.BOLD),
        pair.first,
        pair.second,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return sb
}

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

fun getSimpleFormat(pattern: String) = SimpleDateFormat(pattern, Locale.getDefault())
fun getToday(pattern: String) = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())

fun convertPattern(inputPattern: String, outputPattern: String, input: String): String {
    var output = ""

    try {
        val date = SimpleDateFormat(inputPattern, Locale.getDefault()).parse(input)
        output = SimpleDateFormat(outputPattern, Locale.getDefault()).format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    } finally {
        return output
    }
}

fun getSpannedColorText(origin: String, changed: String, color: Int, bold: Boolean = false): Spannable {
    val sb = SpannableStringBuilder(origin)
    val pair = getChangedIndex(origin, changed)

    sb.setSpan(
        ForegroundColorSpan(color),
        pair.first,
        pair.second,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    if (bold) {
        sb.setSpan(
            StyleSpan(Typeface.BOLD),
            pair.first,
            pair.second,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return sb
}

fun getSpannedSizeText(
    origin: String,
    changed: String,
    sizeDp: Int,
    bold: Boolean = false
): Spannable {

    val sb = SpannableStringBuilder(origin)
    val pair = getChangedIndex(origin, changed)

    sb.setSpan(
        AbsoluteSizeSpan(sizeDp, true),
        pair.first,
        pair.second,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    if (bold) {
        sb.setSpan(
            StyleSpan(Typeface.BOLD),
            pair.first,
            pair.second,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return sb
}

fun getSpannedColorSizeText(
    origin: String,
    changed: String,
    color: Int,
    sizeDp: Int,
    bold: Boolean = false
): Spannable {
    val sb = SpannableStringBuilder(origin)
    val pair = getChangedIndex(origin, changed)

    sb.setSpan(
        AbsoluteSizeSpan(sizeDp, true),
        pair.first,
        pair.second,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    sb.setSpan(
        ForegroundColorSpan(color),
        pair.first,
        pair.second,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    if (bold) {
        sb.setSpan(
            StyleSpan(Typeface.BOLD),
            pair.first,
            pair.second,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return sb
}

inline fun <reified T> Observable<T>.retryWithDelay(maxRetries: Int, retryDelayMillis: Int): Observable<T> {
    var retryCount = 0

    return retryWhen { thObservable ->
        thObservable.flatMap { throwable ->
            if (++retryCount < maxRetries) {
                Log.d("retryWhen", "retryWhen ${T::class.java} retry($retryCount) ...")
                Observable.timer(retryDelayMillis.toLong(), TimeUnit.MILLISECONDS)
            } else {
                Log.d("retryWhen","retryWhen ${T::class.java} retry($retryCount) failed")
                Observable.error(throwable)
            }
        }
    }
}

infix fun Any?.hideKeyboard(currentFocus: View) {
    if (currentFocus is AppCompatEditText) {
        currentFocus.apply {
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
            clearFocus()
        }
    }
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

fun isEditTextEmpty(id: String?, pw: String?) =
    (id?.trim().isNullOrEmpty() || pw?.trim().isNullOrEmpty())

fun isEditTextEmpty(id: String?) = id?.trim().isNullOrEmpty()

fun isEmailPattern(id: String?) =
    (Patterns.EMAIL_ADDRESS.matcher(id?.trim().toString()).matches())

fun isPWDPattern(pw: String) = (PASSWORD_PATTERN.matcher(pw).matches())

private lateinit var mInputMethodManager: InputMethodManager
fun AppCompatEditText.hideKeyboard(activity: FragmentActivity) {
    activity.currentFocus?.let {
        if (!::mInputMethodManager.isInitialized) {
            mInputMethodManager = activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        mInputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        this.clearFocus()
    }
}

fun getSpannedUnderLineBoldText(origin: String, changed: String, isBold: Boolean): Spannable {
    val sb = SpannableStringBuilder(origin)
    val pair = getChangedIndex(origin, changed)
    sb.setSpan(UnderlineSpan(), pair.first, pair.second, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    sb.setSpan(
        StyleSpan(if (isBold) Typeface.BOLD else Typeface.NORMAL),
        pair.first,
        pair.second,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return sb
}

fun getChangedIndex(origin: String, changed: String): Pair<Int, Int> {
    if (origin.isEmpty()) return Pair(0, 0)

    var start = origin.indexOf(changed, 0)
    var end = start + changed.length
    if (start == -1) {
        start = 0
        end = origin.length
    }
    return Pair(start, end)
}

const val STANDARD_WIDTH_DP = 360f  // 360dp 를 기준으로

fun getScreenWidthToPx(): Int = Resources.getSystem().displayMetrics.widthPixels
fun getScreenHeightToPx(): Int = Resources.getSystem().displayMetrics.heightPixels

fun getSizeRelativeScreenWidth(dp: Float): Float
        = getScreenWidthToPx().toFloat() * dp.toPx / STANDARD_WIDTH_DP.toPx

fun getSizeRelativeScreenWidth(px: Int): Float
        = getScreenWidthToPx().toFloat() * px / STANDARD_WIDTH_DP.toPx.toPx

inline val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline val Float.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun getScreenWidth(context: Context, usePx: Boolean): Int {
    val dm = DisplayMetrics()
    val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(dm)
    return if (usePx) dm.widthPixels else (dm.widthPixels / dm.density).roundToInt()
}

fun getScreenHeight(context: Context, usePx: Boolean): Int {
    val dm = DisplayMetrics()
    val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getMetrics(dm)
    return if (usePx) dm.heightPixels else (dm.heightPixels / dm.density).roundToInt()
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