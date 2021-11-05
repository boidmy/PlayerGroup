package com.example.playergroup.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentActivity
import java.util.regex.Pattern
import kotlin.math.roundToInt

val PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{8,15}.\$")   // 8 ~ 16 ( 특수문자, 문자, 숫자 모두 포함 )
const val STANDARD_WIDTH_DP = 360f  // 360dp 를 기준으로

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

infix fun Any?.hideKeyboard(currentFocus: View) {
    if (currentFocus is AppCompatEditText) {
        currentFocus.apply {
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
            clearFocus()
        }
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