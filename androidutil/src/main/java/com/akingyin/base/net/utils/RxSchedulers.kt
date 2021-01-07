/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.utils


import com.akingyin.base.net.exception.ApiException.Companion.isSuccess
import com.akingyin.base.net.mode.ApiResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 调度器
 * @ Description:
 * @ Author king
 * @ Date 2017/9/28 11:29
 * @ Version V1.0
 */
object RxSchedulers {


    /**
     * 简化线程转换(消费事件会在UI线程)
     * @param <T>
     * @return
    </T> */
    fun <T> IO_Main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 线程转换全是IO线程
     * @param <T>
     * @return
    </T> */
    fun <T> IO_IO(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
        }
    }

    /**
     * 以RX方式处理
     * @param t
     * @param <T>
     * @return
    </T> */
    fun <T> createData(t: T): Observable<T> {
        return Observable.create { subscriber ->
            try {
                subscriber.onNext(t)
                subscriber.onComplete()
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }

    /**
     * 处理结果数据
     * @param <T>
     * @return
    </T> */
    fun <T> handleResult(): ObservableTransformer<ApiResult<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.flatMap { apiResult ->
                if (isSuccess(apiResult)) {
                    createData(apiResult.data)
                } else Observable.error(Exception(apiResult.msg))
            }
        }
    }


}