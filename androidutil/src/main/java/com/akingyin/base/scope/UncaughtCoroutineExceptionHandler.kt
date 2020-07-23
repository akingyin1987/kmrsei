/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.scope

import com.akingyin.base.ext.coroutineErrorListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/23 12:21
 * @version V1.0
 */
class UncaughtCoroutineExceptionHandler (private val errorHandler: coroutineErrorListener?=null)
    : CoroutineExceptionHandler, AbstractCoroutineContextElement(CoroutineExceptionHandler.Key){
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        exception.printStackTrace()

        errorHandler?.let {
            it(exception)
        }
    }
}