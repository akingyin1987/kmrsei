/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.mode

import android.text.TextUtils
import com.akingyin.base.net.config.CommonConstants.API_HOST
import java.io.File

object ApiHost {
    private const val HTTP_HEADER = "http://"
    private const val HTTPS_HEADER = "https://"
    private var host = API_HOST
    fun getHost(): String {
        return if (!TextUtils.isEmpty(host) && !host.endsWith(File.separator)) {
            host + File.separator
        } else host
    }

    fun setHost(url: String) {
        host = url
    }

    fun setHostHttp(url: String) {
        if (url.startsWith(HTTPS_HEADER) || url.startsWith(HTTP_HEADER)) {
            host = url
            host = host.replace(HTTPS_HEADER.toRegex(), HTTP_HEADER)
        } else {
            host = HTTP_HEADER + url
        }
    }

    fun setHostHttps(url: String) {
        if (url.startsWith(HTTPS_HEADER) || url.startsWith(HTTP_HEADER)) {
            host = url
            host = host.replace(HTTP_HEADER.toRegex(), HTTPS_HEADER)
        } else {
            host = HTTPS_HEADER + url
        }
    }

    val http: String
        get() {
            if (host.startsWith(HTTPS_HEADER) || host.startsWith(HTTP_HEADER)) {
                host = host.replace(HTTPS_HEADER.toRegex(), HTTP_HEADER)
            } else {
                host = HTTP_HEADER + host
            }
            return host
        }

    fun getHttp(url: String): String {

        var   urlStr = if (url.startsWith(HTTPS_HEADER) || url.startsWith(HTTP_HEADER)) {
            url.replace(HTTPS_HEADER.toRegex(), HTTP_HEADER)
        } else {
            HTTP_HEADER + url
        }
        if (!url.endsWith(File.separator)) {
            urlStr = "$url/"
        }
        return urlStr
    }

    val https: String
        get() {
            if (host.startsWith(HTTPS_HEADER) || host.startsWith(HTTP_HEADER)) {
                host = host.replace(HTTP_HEADER.toRegex(), HTTPS_HEADER)
            } else {
                host = HTTPS_HEADER + host
            }
            return host
        }
}