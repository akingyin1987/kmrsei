/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.util

/**
 * @ Description:
 * @author king
 * @ Date 2020/12/10 11:39
 * @version V1.0
 */
var LOG_ENABLE = true

const val LOG_TAG = "RxDownload"


fun <T> T.log(prefix: String = ""): T {
    if (LOG_ENABLE) {
        if (this is Throwable) {
            Log.w(LOG_TAG, prefix + this.message, this)
        } else {
            Log.d(LOG_TAG, prefix + toString())
        }
    }
    return this
}