/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ext

import android.os.Handler
import android.os.Looper
import com.akingyin.base.scope.SafeCoroutineScope
import kotlinx.coroutines.*

/**
 * 切换调度器
 * @ Description:
 * @author king
 * @ Date 2020/7/23 11:34
 * @version V1.0
 */


val UI: CoroutineDispatcher        = Dispatchers.Main

val IO: CoroutineDispatcher        = Dispatchers.IO

val Default: CoroutineDispatcher   = Dispatchers.Default

val Unconfined:CoroutineDispatcher = Dispatchers.Unconfined

suspend fun <T>  withMain(block:suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main,block)


suspend fun <T>  withIO(block:suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO,block)


suspend fun <T>  withDefault(block:suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default,block)


suspend fun <T>  withUnconfied(block:suspend CoroutineScope.() -> T) = withContext(Dispatchers.Unconfined,block)

// 运行在主线程，支持异常处理、无返回结果
fun runOnUI(block: suspend CoroutineScope.() -> Unit) = uiScope().launch(block = block)

// 运行在后台线程，支持异常处理、无返回结果
fun runInBackground(block: suspend CoroutineScope.() -> Unit) = ioScope().launch(block = block)

// 运行在主线程，支持异常处理、有返回结果
fun <T> asyncOnUI(block: suspend CoroutineScope.() -> T) = uiScope().async(block = block)

// 运行在后台线程，支持异常处理、有返回结果
fun <T> asyncInBackground(block: suspend CoroutineScope.() -> T) = ioScope().async(block = block)


fun ioScope(errorHandler: coroutineErrorListener?=null) = SafeCoroutineScope(IO,errorHandler)

fun uiScope(errorHandler: coroutineErrorListener?=null) = SafeCoroutineScope(UI,errorHandler)

fun defaultScope(errorHandler: coroutineErrorListener?=null) = SafeCoroutineScope(Default,errorHandler)

fun customScope(dispatcher: CoroutineDispatcher, errorHandler: coroutineErrorListener?=null) = SafeCoroutineScope(dispatcher,errorHandler)
/**
 * 在主线程运行
 */
fun runMain(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        Handler(Looper.getMainLooper()).post { block() }
    }
}