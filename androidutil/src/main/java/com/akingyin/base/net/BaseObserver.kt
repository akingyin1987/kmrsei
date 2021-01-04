/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
@file:Suppress("DEPRECATION")

package com.akingyin.base.net

import android.app.ProgressDialog
import android.content.Context

import android.widget.Toast
import com.akingyin.base.net.exception.ApiException
import com.akingyin.base.net.mode.ApiResult
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

import timber.log.Timber
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 对观察者进行简单封装
 * @ Description:
 * @ Author king
 * @ Date 2016/12/30 18:22
 * @ Version V1.0
 * @author zlcd
 */
@Suppress("DEPRECATION")
abstract class BaseObserver<T> : Observer<ApiResult<T>> {
    private var mContextWeakReference: WeakReference<Context?>? = null
    private var mDialog: ProgressDialog? = null
    private var mDisposable: Disposable? = null
    private val SUCCESS_CODE = 0

    constructor()
    constructor(context: Context?, dialog: ProgressDialog?) {
        mContextWeakReference = WeakReference(context)
        mDialog = dialog
        mDialog?.setOnCancelListener { _ -> mDisposable?.dispose() }
    }

    override fun onSubscribe(d: Disposable) {
        mDisposable = d

    }

    override fun onNext(value: ApiResult<T>) {
        if (ApiException.isSuccess(value)) {
            val t = value.data
            onHandleSuccess(t)
        } else {
            onHandleError(value.code, value.msg)
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        mDialog?.dismiss()
        var error = "出错了，" + e.message
        when (e) {
            is SocketTimeoutException -> {
                error = "服务器响应超时，请稍后再试"
            }
            is ConnectException -> {
                error = "服务器连接超时，请稍后再试"
            }
            is UnknownHostException -> {
                error = "出错了，请检查服务器地址是否正确"
            }
        }
        onHandleError(-1, error)
    }

    override fun onComplete() {
        Timber.tag("gesanri").d("onComplete")
        mDialog?.dismiss()
        mDisposable?.dispose()
    }

    /**
     * 2
     * @param t
     */
    abstract fun onHandleSuccess(t: T)
    protected fun onHandleError(code: Int, message: String) {
        if (null != mContextWeakReference && null != mContextWeakReference!!.get()) {
            Toast.makeText(mContextWeakReference!!.get(), message, Toast.LENGTH_LONG).show()
        }
    }
}