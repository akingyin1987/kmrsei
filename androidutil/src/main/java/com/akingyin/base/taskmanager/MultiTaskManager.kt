/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager

import com.akingyin.base.rx.RxUtil.Companion.IO_Main
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum.Companion.getTaskManagerStatus
import com.akingyin.base.taskmanager.enums.TaskStatusEnum
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum
import com.blankj.utilcode.util.StringUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 多任务管理器
 *
 * @author akingyin
 * @date 2019-12-19
 */
class MultiTaskManager private constructor() : ITaskResultCallBack {
    var threadTypeEnum = ThreadTypeEnum.CurrentThread

    /** 线程池  */
    private var threadPool: ExecutorService? = null

    /** 待执行任务  */
    private val queueTasks = LinkedBlockingQueue<AbsTaskRunner>()

    /**  错误日志  */
    var errorStrings = LinkedBlockingQueue<String>()

    /** 任务总数  */
    private val count = AtomicInteger(0)

    /** 成功数  */
    private val successTotal = AtomicInteger(0)

    /** 已执行数  */
    private val overTotal = AtomicInteger(0)

    /** 错误数  */
    private val errorTotal = AtomicInteger(0)

    /** 状态TaskManagerStatusEnum  */
    private val status = AtomicInteger(0)
    private var errorMsg = StringBuilder()
    var lastErrorMsg: String = ""

    private val mCompositeDisposable = CompositeDisposable()
    fun addSubscription(disposable: Disposable?) {
        mCompositeDisposable.add(disposable)
    }

    fun getErrorMsg(): String {
        return errorMsg.toString()
    }

    /**
     * 获取当前任务状态
     * @return
     */
    val taskManagerStatus: TaskManagerStatusEnum?
        get() = getTaskManagerStatus(status.get())
    private var nThreads = 0
    var callBack: ApiTaskCallBack? = null
    fun addTasks(tasks: List<AbsTaskRunner>) {
        for (taskRunner in tasks) {
            addTask(taskRunner)
        }
    }

    fun addTask(task: AbsTaskRunner) {
        task.callBack = this
        try {
            queueTasks.put(task)
            count.getAndIncrement()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun executeTask() {
        var index = 1
        for (taskRunner in queueTasks) {
            if (index <= MAX_CACHE_SIZE) {
                threadPool?.execute(taskRunner)
            }
            index++
        }
    }

    private fun executeTask(postion: Int) {
        var index = 1
        for (taskRunner in queueTasks) {
            if (index >= postion && index < MAX_CACHE_SIZE + postion) {
                threadPool?.execute(taskRunner)
            }
            index++
        }
    }

    fun onPauseTask() {
        if (status.get() == 3 || status.get() == 4) {
            return
        }
        status.getAndSet(6)
        if (null != threadPool) {
            threadPool?.shutdownNow()
        }
    }

    fun onStartTask() {
        errorMsg = StringBuilder()
        lastErrorMsg = ""
        errorStrings.clear()
        status.getAndSet(2)
        if (null != threadPool) {
            threadPool = ThreadPoolExecutor(nThreads, nThreads * 40,
                    0L, TimeUnit.MILLISECONDS,
                    LinkedBlockingQueue(1024),
                    ThreadFactoryBuilder().setNameFormat("multitask-pool-%d").build(), ThreadPoolExecutor.AbortPolicy())
            for (absTaskRunner in queueTasks) {
                if (absTaskRunner.taskStatusEnum.code <= 3) {
                    threadPool?.execute(absTaskRunner)
                }
            }
        }
    }

    val total: Int
        get() {
            println("count=" + count.get() + ":" + queueTasks.size)
            return count.get()
        }

    fun getSuccessTotal(): Int {
        return successTotal.get()
    }

    fun getErrorTotal(): Int {
        return errorTotal.get()
    }

    fun getOverTotal(): Int {
        return overTotal.get()
    }

    val isOver: Boolean
        get() = null == threadPool || total == getOverTotal()

    /**
     * 取消正在下载的任务
     */
    @Synchronized
    fun cancelTasks() {
        println("取消任务---->")
        status.getAndSet(3)
        count.set(0)
        mCompositeDisposable.dispose()
        mCompositeDisposable.clear()
        if (threadPool != null) {
            for (taskRunner in queueTasks) {
                taskRunner.onCancel()
            }
            queueTasks.clear()
            try {
                threadPool?.shutdownNow()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            threadPool = null
        }
    }

    override fun onCallBack(statusEnum: TaskStatusEnum, error: String) {

        if (status.get() == 4 || status.get() == 3 || status.get() == 5) {
            return
        }
        if (!StringUtils.isEmpty(error)) {
            errorMsg.append(error).append(" ")
            lastErrorMsg = error
            errorStrings.offer(error)
        }
        when (statusEnum) {
            TaskStatusEnum.NETERROR -> cancelTasks()
            TaskStatusEnum.SUCCESS -> {
                overTotal.getAndIncrement()
                successTotal.getAndIncrement()
            }
            else -> {
                overTotal.getAndIncrement()
                errorTotal.getAndIncrement()
            }
        }
        if (overTotal.get() > 0 && overTotal.get() % MAX_CACHE_SIZE == 0 && count.get() > overTotal.get()) {
            println("执行下一轮-->")
            executeTask(overTotal.get())
        }
        if (null != callBack) {
            if (statusEnum !== TaskStatusEnum.NETERROR) {
                val errorNum = errorTotal.get()
                val sucNum = successTotal.get()
                if (threadTypeEnum === ThreadTypeEnum.MainThread) {
                    val disposable = Observable.just(count.get()).compose(IO_Main())
                            .subscribe(
                                    { integer: Int? -> callBack?.onCallBack(count.get(), errorNum + sucNum, errorNum) }) { obj: Throwable -> obj.printStackTrace() }
                    addSubscription(disposable)
                } else {
                    callBack?.onCallBack(count.get(), errorNum + sucNum, errorNum)
                }
                if (count.get() == errorNum + sucNum) {
                    status.getAndSet(4)
                    if (errorNum > 0) {
                        val disposable = Observable.just(count.get()).compose(IO_Main())
                                .subscribe(
                                        { integer: Int? -> callBack?.onError(errorMsg.toString(), TaskManagerStatusEnum.COMPLETE) }) { obj: Throwable -> obj.printStackTrace() }
                        addSubscription(disposable)
                    } else {
                        val disposable = Observable.just(count.get()).compose(IO_Main())
                                .subscribe(
                                        { integer: Int? -> callBack?.onComplete() }) { obj: Throwable -> obj.printStackTrace() }
                        addSubscription(disposable)
                    }
                }
            } else {
                status.getAndSet(5)
                val disposable = Observable.just(count.get()).compose(IO_Main())
                        .subscribe(
                                { integer: Int? -> callBack?.onError(error, TaskManagerStatusEnum.NETError) }) { obj: Throwable -> obj.printStackTrace() }
                addSubscription(disposable)
            }
        }
    }

    override fun onCancelAllTask(statusEnum: TaskManagerStatusEnum, error: String) {
        if (null != callBack) {
            val disposable = Observable.just(count.get()).compose(IO_Main())
                    .subscribe(
                            { integer: Int? -> callBack?.onError(error, statusEnum) }) { obj: Throwable -> obj.printStackTrace() }
            addSubscription(disposable)
        }
        if (statusEnum === TaskManagerStatusEnum.CANCEL ||
                statusEnum === TaskManagerStatusEnum.COMPLETE) {
            cancelTasks()
        }
    }

    companion object {
        /**
         * // 创建线程池，核心线程数、最大线程数、空闲保持时间、队列长度、拒绝策略可自行定义
         * @param poolSize
         * @return
         */
        @JvmStatic
        fun createPool(poolSize: Int): MultiTaskManager {
            val taskManager = MultiTaskManager()

            // 创建线程池，核心线程数、最大线程数、空闲保持时间、队列长度、拒绝策略可自行定义
            taskManager.threadPool = ThreadPoolExecutor(poolSize, poolSize * 40,
                    0L, TimeUnit.MILLISECONDS,
                    LinkedBlockingQueue(MAX_CACHE_SIZE),
                    ThreadFactoryBuilder().setNameFormat("multitask-pool-%d").build(), ThreadPoolExecutor.AbortPolicy())
            taskManager.nThreads = poolSize
            return taskManager
        }

        var MAX_CACHE_SIZE = 1024
    }
}