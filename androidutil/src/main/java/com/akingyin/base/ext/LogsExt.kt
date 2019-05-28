package com.akingyin.base.ext

import com.orhanobut.logger.Logger

/**
 * 日志扩展类
 * @ Description:
 * @author king
 * @ Date 2019/5/28 17:03
 * @version V1.0
 */

fun Any.logDebug() = Logger.d(this)

fun Any.logDebug(tag: String) = Logger.d(tag, this)

fun String.logError() = Logger.e(this)

fun Throwable.logError() = Logger.e(this.message.orEmpty())

fun String.logJson() = Logger.json(this)

inline fun <reified T> T.logJson() = Logger.json(this.toJson())