package com.akingyin.base.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers
import org.reactivestreams.Subscription

/**
 * @ Description:
 * @ Author king
 * @ Date 2016/12/30 10:49
 * @ Version V1.0
 */
class RxUtil {


    companion object {
        fun unsubscribe(subscription: Subscription?) {
            subscription?.cancel()
        }

        /**
         * 简化线程转换(消费事件会在UI线程)
         * @param <T>
         * @return
        </T> */
        @JvmStatic
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
         * 生成Observable
         *
         * @param <T>
         * @return
        </T> */
        fun <T> createData(t: T): Observable<T> {
            return Observable.create { subscriber ->
                try {
                    subscriber.onNext(t)
                    subscriber.onComplete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    subscriber.onError(e.cause)
                }
            }
        }
    }
}