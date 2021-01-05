package com.akingyin.base.rx

import com.akingyin.base.net.exception.ApiException
import io.reactivex.rxjava3.core.Observable

import io.reactivex.rxjava3.exceptions.CompositeException
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/11/10 17:36
 */
class RetryWhenNetworkException :  Function<Observable<out Throwable>, Observable<*>>{
    private var count = 3
    private var delay: Long = 1000
    private var increaseDelay: Long = 1000
    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable
                .zipWith(Observable.range(1, count + 1), { t1, t2 -> Wrapper(t1, t2) })
                .flatMap(Function<Wrapper, Observable<*>> { wrapper ->
                    if ((wrapper.throwable is ConnectException
                                    || wrapper.throwable is SocketTimeoutException
                                    || wrapper.throwable is TimeoutException || wrapper.throwable is ApiException) && wrapper.index < count + 1) { //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                        return@Function Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS)
                    }
                    Observable.error<Throwable>(wrapper.throwable)
                })
    }

    private inner class Wrapper(throwable: Throwable, val index: Int) {
        var throwable: Throwable? = null

        init {
            if (throwable is CompositeException) {
                this.throwable = throwable.exceptions[0]
            } else {
                this.throwable = throwable
            }

        }
    }
}