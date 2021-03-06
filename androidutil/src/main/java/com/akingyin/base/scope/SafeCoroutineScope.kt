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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/23 12:15
 * @version V1.0
 */
class SafeCoroutineScope (context: CoroutineContext, errorHandler: coroutineErrorListener?=null): CoroutineScope,Closeable{

    override val coroutineContext: CoroutineContext = SupervisorJob() + context + UncaughtCoroutineExceptionHandler(errorHandler)
    override fun close() {
        coroutineContext.cancelChildren()
    }
}