package com.akingyin.base.mvvm.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/31 17:55
 * @version V1.0
 */
open  class BaseViewModel :ViewModel(),LifecycleObserver, CoroutineScope {

    val mException: MutableLiveData<Throwable> = MutableLiveData()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val mLaunchManager: MutableList<Job> = mutableListOf()


    protected fun launchOnUITryCatch(tryBlock: suspend CoroutineScope.() -> Unit,
                                     cacheBlock: suspend CoroutineScope.(Throwable) -> Unit,
                                     finallyBlock: suspend CoroutineScope.() -> Unit,
                                     handleCancellationExceptionManually: Boolean
    ) {
        launchOnUI {
            tryCatch(tryBlock, cacheBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }
    private  fun  launchOnUI(block :suspend CoroutineScope.() ->Unit){
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
                    mException.value = e
                    catchBlock(e)
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestory() {
        Timber.i("onDestory")
        clearLaunchTask()
    }

    private fun clearLaunchTask() {
        mLaunchManager.clear()
    }
}