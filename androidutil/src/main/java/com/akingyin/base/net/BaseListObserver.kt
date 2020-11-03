/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net

import android.app.ProgressDialog
import android.content.Context
import com.akingyin.base.net.mode.ApiListResult
import timber.log.Timber
import android.widget.Toast
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.MessageFormat

/**
 * @ Description:
 * @author king
 * @ Date 2016/12/31 17:06
 * @ Version V1.0
 */

@Suppress("DEPRECATION")
abstract class BaseListObserver<T> @JvmOverloads constructor(private val context: Context?=null, private val dialog: ProgressDialog?= null) : Observer<ApiListResult<T>> {

    private var mDisposable: Disposable? = null
    private val SUCCESS_CODE = 0



    override fun onSubscribe(d: Disposable?) {
        mDisposable = d
    }

    override fun onNext(result: @NonNull ApiListResult<T>) {
        if (result.code == SUCCESS_CODE) {

            onHandleSuccess(result.data)
        } else {
            onHandleError(result.code, result.msg)
        }
    }

    override fun onError(e: Throwable) {
        Timber.d(MessageFormat.format("error:{0}", e.toString()))
        dialog?.dismiss()
        var error = "网络异常，请稍后再试"
        if (e is SocketTimeoutException) {
            error = "服务器响应超时，请稍后再试"
        } else if (e is ConnectException) {
            error = "服务器连接超时，请稍后再试"
        } else if (e is UnknownHostException) {
            error = "出错了，请检查服务器地址是否正确"
        }
        onHandleError(-1, error)
    }

    override fun onComplete() {
        Timber.tag("gesanri").d("onComplete")
        dialog?.dismiss()
        mDisposable?.dispose()
    }

    /**
     *
     * @param datas
     */
    abstract fun onHandleSuccess(datas: List<T>)
    protected fun onHandleError(code: Int, message: String) {
        if (null != context) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}