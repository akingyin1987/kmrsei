/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net

import io.reactivex.rxjava3.functions.Consumer
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/2/28 17:54
 * @ Version V1.0
 */
open class RxException<T : Throwable?>(private val onError: Consumer<in Throwable>) : Consumer<T> {
    @Throws(Throwable::class)
    override fun accept(t: T) {
        when (t) {
            is SocketTimeoutException -> {

                onError.accept(Throwable(SOCKETTIMEOUTEXCEPTION))
            }
            is ConnectException -> {

                onError.accept(Throwable(CONNECTEXCEPTION))
            }
            is UnknownHostException -> {

                onError.accept(Throwable(UNKNOWNHOSTEXCEPTION))
            }
            else -> {

                onError.accept(t)
            }
        }
    }

    companion object {
        private const val TAG = "RxException"
        private const val SOCKETTIMEOUTEXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试"
        private const val CONNECTEXCEPTION = "网络连接异常，请检查您的网络状态"
        private const val UNKNOWNHOSTEXCEPTION = "网络异常，请检查您的网络状态"
    }
}