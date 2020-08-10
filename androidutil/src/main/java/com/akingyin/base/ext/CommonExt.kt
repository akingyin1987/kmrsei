package com.akingyin.base.ext

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import java.io.Serializable

/**
 * @ Description:
 * @author king
 * @ Date 2019/4/29 11:59
 * @version V1.0
 */
inline val app: Application
    get() = Ext.ctx

inline val currentTimeMillis: Long
    get() = System.currentTimeMillis()

inline val  appServerTime:Long
    get() = AppTime.getAppTime()

fun findColor(@ColorRes resId: Int) = ContextCompat.getColor(app, resId)

fun findDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(app, resId)

fun findColorStateList(@ColorRes resId: Int): ColorStateList? = ContextCompat.getColorStateList(app, resId)


fun inflate(@LayoutRes layoutId: Int, parent: ViewGroup?, attachToRoot: Boolean = false) = LayoutInflater.from(app).inflate(layoutId, parent, attachToRoot)!!

fun inflate(@LayoutRes layoutId: Int) = inflate(layoutId, null)

fun Context.dial(tel: String?) = startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)))

fun Context.sms(phone: String?, body: String = "") {
    val smsToUri = Uri.parse("smsto:" + phone)
    val intent = Intent(Intent.ACTION_SENDTO, smsToUri)
    intent.putExtra("sms_body", body)
    startActivity(intent)
}

fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

@Suppress("DEPRECATION")
fun isNetworkConnected(): Boolean {
    val mNetworkInfo = connectivityManager.activeNetworkInfo
    if (mNetworkInfo != null) {
        return mNetworkInfo.isAvailable
    }
    return false
}


////强行三目运算符 yes no
//val value = bool.yes { "true value" }.no { "false value" }
infix fun <T> Boolean.yes(trueValue: () -> T) = TernaryOperator(trueValue, this)

class TernaryOperator<out T>(val trueValue: () -> T, val bool: Boolean)

infix inline fun <T> TernaryOperator<T>.no(falseValue: () -> T) = if (bool) trueValue() else falseValue()


/**
 * 数组转bundle
 */
@Suppress("UNCHECKED_CAST")
fun Array<out Pair<String, Any?>>.toBundle(): Bundle {
    return Bundle().apply {
        forEach {
            when (val value = it.second) {
                null -> putSerializable(it.first, null as Serializable?)
                is Int -> putInt(it.first, value)
                is Long -> putLong(it.first, value)
                is CharSequence -> putCharSequence(it.first, value)
                is String -> putString(it.first, value)
                is Float -> putFloat(it.first, value)
                is Double -> putDouble(it.first, value)
                is Char -> putChar(it.first, value)
                is Short -> putShort(it.first, value)
                is Boolean -> putBoolean(it.first, value)
                is Serializable -> putSerializable(it.first, value)
                is Parcelable -> putParcelable(it.first, value)

                is IntArray -> putIntArray(it.first, value)
                is LongArray -> putLongArray(it.first, value)
                is FloatArray -> putFloatArray(it.first, value)
                is DoubleArray -> putDoubleArray(it.first, value)
                is CharArray -> putCharArray(it.first, value)
                is ShortArray -> putShortArray(it.first, value)
                is BooleanArray -> putBooleanArray(it.first, value)

                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> putCharSequenceArray(it.first, value as Array<CharSequence>)
                    value.isArrayOf<String>() -> putStringArray(it.first, value as Array<String>)
                    value.isArrayOf<Parcelable>() -> putParcelableArray(it.first, value as Array<Parcelable>)
                }
            }
        }
    }

}


inline fun tryCatch(tryBlock: () -> Unit, catchBlock: (Throwable) -> Unit = {}) {
    try {
        tryBlock()
    } catch (t: Throwable) {
        t.printStackTrace()
        catchBlock.invoke(t)

    }
}

/**
 * 批量设置控件点击事件。
 *
 * @param v 点击的控件
 * @param block 处理点击事件回调代码块
 */
fun setOnClickListener(vararg v: View?, block: View.() -> Unit) {
    val listener = View.OnClickListener { it.block() }
    v.forEach { it?.setOnClickListener(listener) }
}

/**
 * 批量设置控件点击事件。
 *
 * @param v 点击的控件
 * @param listener 处理点击事件监听器
 */
fun setOnClickListener(vararg v: View?, listener: View.OnClickListener) {
    v.forEach { it?.setOnClickListener(listener) }
}


