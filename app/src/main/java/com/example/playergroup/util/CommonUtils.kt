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
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.example.playergroup.BuildConfig
import io.reactivex.Observable
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

infix fun Context.debugToast(message: () -> String) {
    if (BuildConfig.DEBUG) Toast.makeText(this, message(), Toast.LENGTH_SHORT).show()
}
infix fun View?.click(block: (View) -> Unit) = this?.setOnClickListener(block)
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