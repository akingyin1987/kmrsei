package com.akingyin.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlin.coroutines.CoroutineContext


/**
 * @ Description:
 * @author king
 * @ Date 2018/8/3 15:59
 * @version V1.0
 */
open  class  BasePresenter <T:IBaseView> :IPresenter<T> , CoroutineScope {


    override val coroutineContext: CoroutineContext
        get() =  Dispatchers.Main


    var mRootView: T? = null
        private set

     var compositeDisposable = CompositeDisposable()




    override fun attachView(mRootView: T) {
        this.mRootView = mRootView
        initialization()
    }


     val mLaunchManager: MutableList<Job> = mutableListOf()

    protected fun launchOnIOTryCatch(tryBlock: suspend CoroutineScope.() -> Unit,
                                     cacheBlock: suspend CoroutineScope.(  Throwable) -> Unit,
                                     handleCancellationExceptionManually: Boolean=true
    ) {
        launchOnIO {
            tryCatch(tryBlock, cacheBlock, {}, handleCancellationExceptionManually)
        }
    }


    protected  fun  launchOnIO(block :suspend CoroutineScope.() ->Unit){
        val job = launch(IO) { block() }
        mLaunchManager.add(job)
        job.invokeOnCompletion { mLaunchManager.remove(job) }
    }


    protected fun launchOnUITryCatch(tryBlock: suspend CoroutineScope.() -> Unit,
                                    cacheBlock: suspend CoroutineScope.(  Throwable) -> Unit,
                           handleCancellationExceptionManually: Boolean=true
    ) {
        launchOnUI {
            tryCatch(tryBlock, cacheBlock, {}, handleCancellationExceptionManually)
        }
    }
    protected fun launchOnUITryCatch(tryBlock: suspend CoroutineScope.() -> Unit,
                                     cacheBlock: suspend CoroutineScope.(Throwable) -> Unit,
                                     finallyBlock: suspend CoroutineScope.() -> Unit,
                                     handleCancellationExceptionManually: Boolean
    ) {
        launchOnUI {
            tryCatch(tryBlock, cacheBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }
    protected  fun  launchOnUI(block :suspend CoroutineScope.() ->Unit){
        val job = launch { block() }
        mLaunchManager.add(job)
        job.invokeOnCompletion { mLaunchManager.remove(job) }
    }

    private suspend fun tryCatch(
            tryBlock: suspend CoroutineScope.() -> Unit,
            catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
            finallyBlock: suspend CoroutineScope.() -> Unit,
            handleCancellationExceptionManually: Boolean = false) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Throwable) {
                if (e !is CancellationException || handleCancellationExceptionManually) {

                    catchBlock(e)
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }





    override fun detachView() {
        mRootView = null
        mLaunchManager.forEach {
            if(!it.isCancelled){
                it.cancel()
            }
        }
        mLaunchManager.clear()

        //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }

    }



    private val isViewAttached: Boolean
        get() = mRootView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun addJob(job: Job){
        mLaunchManager.add(job)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")

    override fun initialization() {

    }


    fun   onCancel(){
        mLaunchManager.forEach {
            if(!it.isCancelled){
                it.cancel()
            }
        }
        mLaunchManager.clear()
        //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}