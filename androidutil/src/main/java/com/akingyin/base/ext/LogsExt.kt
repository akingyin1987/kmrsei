package com.akingyin.base.ext

import timber.log.Timber

/**
 * 日志扩展类
 * @ Description:
 * @author king
 * @ Date 2019/5/28 17:03
 * @version V1.0
 */

fun Any.logDebug() = Timber.d(this.toString())

fun Any.logDebug(tag: String) = Timber.tag(tag).d( this.toString())

fun String.logError() = Timber.e(this)

fun Throwable.logError() = Timber.e(this.message.orEmpty())

fun String.logJson() = Timber.d(this.toJson())

inline fun <reified T> T.logJson() = Timber.d(this.toJson())