package com.akingyin.base.utils

import android.text.Spanned
import androidx.core.text.HtmlCompat
import java.lang.StringBuilder

object HtmlUtils {
    const val SPACE = "&nbsp;"
    fun getRedHtml(content: String): String {
        return "<font color=#FF0000>$content</font>"
    }

    fun getManySpace(number : Int ):String{
        val stringBuilder = StringBuilder()
        for (i in 0.. number){
           stringBuilder.append(SPACE)
        }
        return stringBuilder.toString()
    }

    fun getGreenHtml(content: String): String {
        return "<font color=#009966>$content</font>"
    }

    fun getBlueHtml(content: String): String {
        return "<font color=#0000FF>$content</font>"
    }

    fun getColorHtml(content: String, color: String): String {
        return "<font color=$color>$content</font>"
    }

    fun getSmallColorHtml(content: String, color: String): String {
        return "<font color=$color><small>$content</small></font>"
    }

    fun getReadPHtml(content: String): String {
        return "<p style=font-size:30px;color:red>$content</p>"
    }

    fun getBigColorHtml(content: String, color: String): String {
        return "<font color=$color><big>$content</big></font>"
    }

    fun getBigStrongColorHtml(content: String, color: String): String {
        return "<font color=$color><strong><big>$content</big></strong></font>"
    }

    fun getBigRedHtml(content: String): String {
        return "<font color=#FF0000><big>$content</big></font>"
    }

    fun getBigStrongRedHtml(content: String): String {
        return "<font color=#FF0000><strong><big>$content</big></strong></font>"
    }

    fun getTelNumberHtml(phoneNumber: String): String {
        return "<a href='tel:$phoneNumber'>$phoneNumber</a>"
    }

    fun getTextHtml(content: String): Spanned {
        return HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}