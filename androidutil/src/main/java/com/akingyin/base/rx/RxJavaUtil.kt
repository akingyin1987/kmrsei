/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.rx

import androidx.annotation.NonNull

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/18 11:28
 * @version V1.0
 */
object RxJavaUtil {

    private val TAG = "RxJavaUtils"


    /**
     * 在ui线程中工作
     *
     * @param uiTask        在UI线程中操作的任务
     * @param errorConsumer 出错的处理
     * @param <T>
     * @return
    </T> */
    fun  doInUIThread(@NonNull uiTask: ()->Unit, @NonNull errorConsumer: Consumer<Throwable>?): Disposable {
        return Flowable.just(uiTask)
                .observeOn(AndroidSchedulers.mainThread())
                .autoDispose(Completable.complete())
                .subscribe(Consumer {
                    it.invoke()
                }, errorConsumer)
    }

    /**
     * 执行异步任务（IO线程处理，UI线程显示）
     *
     * @param t           处理入参
     * @param transformer 转化器
     * @return
     */
    fun < R> executeAsyncTask(ioTask:() -> R,uiTask: (R) -> Unit): Disposable {
      return  Flowable.just(ioTask).map {
          it.invoke()
      }.observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .autoDispose(Completable.complete())
              .subscribe({
                  uiTask.invoke(it)
              },{
                  it.printStackTrace()
              })
    }


    /**
     * 统一线程处理
     * @param <T>
     * @return
    </T> */
    fun <T> rxSchedulerMain_IO(): FlowableTransformer<T, T> {    //compose简化线程
        return FlowableTransformer<T, T> {
            it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }


    fun <T> createObservableData(t: T): Observable<T> {
        return Observable.create { emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            }catch (e:Exception){
                e.printStackTrace()
                emitter.onError(e)
            }

        }
    }

    /**
     * 创建Flowable 定阅
     */
    fun <T> createData(t: T): Flowable<T> {
        return Flowable.create({ emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            }catch (e:Exception){
                e.printStackTrace()
                emitter.onError(e)
            }

        }, BackpressureStrategy.BUFFER)
    }


    fun test(){

       createData(1).autoDispose(AndroidLifecycleScopeProvider.UNBOUND).subscribe {
           println(it)
       }
    }
}