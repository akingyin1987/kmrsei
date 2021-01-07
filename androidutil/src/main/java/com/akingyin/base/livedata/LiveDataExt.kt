/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.akingyin.base.net.Result

/**
 * @ Description:
 * @author king
 * @ Date 2021/1/7 13:21
 * @version V1.0
 */
inline fun <E : Any> LiveData<Result<E>>.handleResult(
        owner: LifecycleOwner, crossinline handler: HhResultHandler<E>.() -> Unit
) {
    val responseHandler = HhResultHandler<E>().apply(handler)
    observe(owner) {
        when (it) {
            is Result.Success -> responseHandler.invokeSuccess(it.data)
            is Result.Failure -> responseHandler.invokeFailure(it.exception)
        }
    }
}