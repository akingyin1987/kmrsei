package com.akingyin.base.ext

import android.text.TextUtils
import java.math.BigDecimal
import java.security.MessageDigest
import java.text.MessageFormat
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2019/4/29 15:24
 * @version V1.0
 */

fun String.isEmptyOrNull():String{
   if(TextUtils.isEmpty(this)){
       return ""
   }
   return this
}

fun String.toast(isShortToast: Boolean = true) = toast(this, isShortToast)

fun String.md5() = encrypt(this, "MD5")

fun String.sha1() = encrypt(this, "SHA-1")

fun ByteArray.sha1() = encrypt(this,"SHA-1")

fun String.isIdcard(): Boolean {
    val p18 = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]\$".toRegex()
    val p15 = "^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]\$".toRegex()
    return matches(p18) || matches(p15)
}

fun String.isPhone(): Boolean {
    val p = "^1([34578])\\d{9}\$".toRegex()
    return matches(p)
}

fun String.isEmail(): Boolean {
    val p = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$".toRegex()
    return matches(p)
}

fun String.isNumeric(): Boolean {
    val p = "^[0-9]+$".toRegex()
    return matches(p)
}



fun String.equalsIgnoreCase(other: String) = this.toLowerCase(Locale.ROOT).contentEquals(other.toLowerCase(Locale.ROOT))

private fun encrypt(string: String, type: String): String {
    val bytes = MessageDigest.getInstance(type).digest(string.toByteArray())
    return bytes2Hex(bytes)
}

private fun encrypt(data: ByteArray, type: String):String{
    val bytes = MessageDigest.getInstance(type).digest(data)
    return bytes2Hex(bytes)
}
fun String.messageFormat(vararg arguments:Any):String{
    return MessageFormat.format(this,*arguments)
}

internal fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}

fun Long.formatSize(): String {
    require(this >= 0) { "Size must larger than 0." }

    val byte = this.toDouble()
    val kb = byte / 1024.0
    val mb = byte / 1024.0 / 1024.0
    val gb = byte / 1024.0 / 1024.0 / 1024.0
    val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0

    return when {
        tb >= 1 -> "${tb.decimal(2)} TB"
        gb >= 1 -> "${gb.decimal(2)} GB"
        mb >= 1 -> "${mb.decimal(2)} MB"
        kb >= 1 -> "${kb.decimal(2)} KB"
        else -> "${byte.decimal(2)} B"
    }
}

fun Double.decimal(digits: Int): Double {
    return this.toBigDecimal()
            .setScale(digits, BigDecimal.ROUND_HALF_UP)
            .toDouble()
}

infix fun Long.ratio(bottom: Long): Double {
    if (bottom <= 0) {
        return 0.0
    }
    val result = (this * 100.0).toBigDecimal().divide((bottom * 1.0).toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
    return result.toDouble()
}