package com.akingyin.base.ext

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat


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