package com.akingyin.base.rx

import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * @ Description:
 * @author king
 * @ Date 2019/6/28 11:26
 * @version V1.0
 */
fun <T> Flowable<T>.retryWithDelay(maxRetries: Int, delayInMillis: Long) = retryWhen (RetryWithDelayFlowableFunction(maxRetries,delayInMillis))


fun <T> Observable<T>.retryWithDelay(maxRetries: Int, delayInMillis: Long)= retryWhen(RetryWithDelayObservableFunction(maxRetries,delayInMillis))

private   class    RetryWithDelayObservableFunction(private  val maxRetries: Int, private  val delayInMillis: Long) : Function<Observable<out  Throwable>,Observable<*>>{

    private var retryCount = 0

    override fun apply(attempts: Observable<out Throwable>): Observable<*> {
        return   attempts.flatMap { throwable ->
            if(retryCount++ < maxRetries){
                Observable.timer(delayInMillis,TimeUnit.MILLISECONDS)
            }else{
                Observable.error(throwable)
            }

        }
    }
}

private   class    RetryWithDelayFlowableFunction(private  val maxRetries: Int, private  val delayInMillis: Long) : Function<Flowable<out  Throwable>,Flowable<*>>{

    private var retryCount = 0
    override fun apply(attempts: Flowable<out Throwable>): Flowable<*> {
      return   attempts.flatMap { throwable ->
             if(retryCount++ < maxRetries){
                 Flowable.timer(delayInMillis,TimeUnit.MILLISECONDS)
             }else{
                 Flowable.error(throwable)
             }

         }
    }
}