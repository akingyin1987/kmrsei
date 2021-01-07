/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.livedata

/**
 * @ Description:
 * @author king
 * @ Date 2021/1/7 13:19
 * @version V1.0
 */
class HhResultHandler<T> {
    private var success: ((T) -> Unit)? = null
    private var failure: ((Throwable) -> Unit)? = null

    infix fun onSuccess(block: (T) -> Unit) {
        success = block
    }

    infix fun onFailure(block: (e: Throwable) -> Unit) {
        failure = block
    }

    fun invokeSuccess(content: T) {
        success?.invoke(content)
    }

    fun invokeFailure(throwable: Throwable) {
        failure?.invoke(throwable)
    }
}