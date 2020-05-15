package com.akingyin.base.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.net.Result
import com.akingyin.base.net.ResultList
import com.akingyin.base.net.exception.ApiException
import com.akingyin.base.net.mode.ApiCode
import com.akingyin.base.net.mode.ApiListResult
import com.akingyin.base.net.mode.ApiResult
import com.akingyin.base.repo.Resource
import com.akingyin.base.repo.StateActionEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext



/**
 * @ Description:
 * @author king
 * @ Date 2019/7/31 17:55
 * @version V1.0
 */
open  class BaseViewModel :AutoDisposeViewModel(), CoroutineScope {


    val mException: MutableLiveData<ApiException> = MutableLiveData()

    //通用事件模型驱动(如：显示对话框、取消对话框、错误提示)
    val mStateLiveData = SingleLiveEvent<StateActionEvent>()
    private var mCompositeDisposable: CompositeDisposable? = null

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
                    mException.value = ApiException(e)
                    catchBlock(e)
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }



      suspend fun <T : Any> safeApiCall(call: suspend () -> ApiResult<T>,errorCall: (error:Int,msg:String) -> Unit): T?{
         val result : Result<T> = safeApiResult(call)
         var data : T? = null
         when(result){
             is Result.Success ->{
                 data = result.data
             }

             is Result.Error ->{

                 errorCall(result.exception.code,result.exception.msg)
             }
         }
         return  data
    }
     suspend fun <T: Any> safeApiResult(call: suspend ()-> ApiResult<T>) :Result<T> {
        val response = call.invoke()
        if(response.code == ApiCode.Http.SUCCESS){
            return response.data?.let {
                Result.Success(it,response.time)
            }?:Result.Error(ApiException("获取数据为空！").apply {
                code = response.code
            })
        }
         println("msg=${response.msg}")
        return  Result.Error(ApiException(response.msg).apply {
            code = response.code
        })

    }

    suspend fun <T: Any> safeApiListResult(call: suspend ()-> ApiListResult<T>) :ResultList<T> {
        val response = call.invoke()
        if(response.code == ApiCode.Http.SUCCESS){
            return  response.data?.let {
                ResultList.Success(it,response.time)
            }?:ResultList.Error(ApiException("获取数据为空！").apply {
                code = response.code
            })

        }
        println("msg=${response.msg}")
        return  ResultList.Error(ApiException(response.msg).apply {
            code = response.code
        })

    }



    protected fun addDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable?.apply {
            if(!isDisposed){
                dispose()
            }
        }

    }

    private fun clearLaunchTask() {

        mLaunchManager.clear()
    }
}