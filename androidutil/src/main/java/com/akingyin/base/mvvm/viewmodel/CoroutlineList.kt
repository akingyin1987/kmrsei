package com.akingyin.base.mvvm.viewmodel


import com.akingyin.base.mvvm.job.BaseJob
import com.akingyin.base.net.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

import kotlin.coroutines.CoroutineContext

class CoroutlineList<T : Any>(val scope: CoroutineScope,
                              var context: CoroutineContext = Dispatchers.IO,
                              var progressCall: (progress: Int, error: Int, total: Int,errorMsg:String) -> Unit,
                              var onErrorCall:(error:Throwable)->Unit,
                              var listJobs:List<BaseJob<T>>,
                              ) {
    var  total = 0
    var  progress:Int =0
    var  error:Int = 0

    var job: Coroutine<Unit>?= null
    init {

       total = listJobs.size

    }

    fun   isComplete() = progress >= total

    fun  onStart(){
        job?.cancel()
        job = Coroutine.async(scope,context){

             listJobs.forEachIndexed { index, baseJob ->
                if(progress in index until total){
                    baseJob.run().let {
                        result ->
                        progress++
                        withContext(Main){
                            when(result){
                                is Result.Success->{
                                    println("成功")
                                    progressCall.invoke(progress,error,total,"")
                                }
                                is Result.Failure->{
                                    println("错误")
                                    error++
                                    progressCall.invoke(progress,error,total,result.exception.message?:"")
                                    if(progress< total){
                                        onErrorCall.invoke(result.exception)
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }.onSuccess {

        }.onError {
            job?.cancel()
            onErrorCall.invoke(it)
        }
    }

    fun onPause(){
        job?.cancel()
    }

    companion object {

        val DEFAULT = GlobalScope

        fun <T : Any> async(
                scope: CoroutineScope = DEFAULT,
                context: CoroutineContext = Dispatchers.IO,
                progressCall: (progress: Int, error: Int, total: Int,errorMsg:String) -> Unit,
                 onErrorCall:(error:Throwable)->Unit,
                listJobs:List<BaseJob<T>>

        ): CoroutlineList<T> {

            return CoroutlineList(scope, context, progressCall,onErrorCall, listJobs)
        }



    }


}