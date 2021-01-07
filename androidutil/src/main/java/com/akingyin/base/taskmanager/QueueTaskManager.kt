/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager

import android.os.Handler
import android.os.Looper
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum.Companion.getTaskManagerStatus
import com.akingyin.base.taskmanager.enums.TaskStatusEnum
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 多任务管理器 队列one by one
 * Created by Administrator on 2016/7/3.
 */
class QueueTaskManager : ITaskResultCallBack {
    var threadTypeEnum = ThreadTypeEnum.CurrentThread
    private val mainHandler = Handler(Looper.getMainLooper())

    /** 线程池  */
    private var threadPool: ExecutorService?
    private val queueTasks = LinkedBlockingQueue<AbsTaskRunner>()

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

    //获取当前任务状态
    val taskManagerStatus: TaskManagerStatusEnum?
        get() = getTaskManagerStatus(status.get())
    var callBack: ApiTaskCallBack? = null
    fun addTask(tasks: List<AbsTaskRunner>) {
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
        for (taskRunner in queueTasks) {
            threadPool!!.execute(taskRunner)
        }
    }

    val total: Int
        get() = count.get()

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
        get() = if (threadPool == null) {
            true
        } else queueTasks.size <= 0

    fun doNext() {
        if (isOver) {
            return
        }
        val qt = queueTasks.poll()
        if (qt != null && null != threadPool) {
            threadPool!!.execute(qt)
        }
    }

    /**
     * 取消正在下载的任务
     */
    @Synchronized
    fun cancelTasks() {
        status.getAndSet(3)
        if (threadPool != null) {
            for (taskRunner in queueTasks) {
                taskRunner.onCancel()
            }
            queueTasks.clear()
            try {
                threadPool!!.shutdownNow()
            } catch (e: Exception) {
            }
            threadPool = null
        }
    }

    override fun onCallBack(statusEnum: TaskStatusEnum, error: String) {
        if (status.get() == 4 || status.get() == 3 || status.get() == 5) {
            return
        }
        overTotal.getAndIncrement()
        when (statusEnum) {
            TaskStatusEnum.NETERROR -> cancelTasks()
            TaskStatusEnum.SUCCESS -> {
                overTotal.getAndIncrement()
                successTotal.getAndIncrement()
                doNext()
            }
            TaskStatusEnum.ERROR -> {
                doNext()
                overTotal.getAndIncrement()
                errorTotal.getAndIncrement()
            }
            else -> {
                overTotal.getAndIncrement()
                errorTotal.getAndIncrement()
            }
        }
        if (null != callBack) {
            if (statusEnum !== TaskStatusEnum.NETERROR) {
                val errorNum = errorTotal.get()
                val sucNum = successTotal.get()
                if (threadTypeEnum === ThreadTypeEnum.MainThread) {
                    mainHandler.post { callBack?.onCallBack(count.get(), errorNum + sucNum, errorNum) }
                } else {
                    callBack?.onCallBack(count.get(), errorNum + sucNum, errorNum)
                }
                if (count.get() == errorNum + sucNum) {
                    status.getAndSet(4)
                    if (errorNum > 0) {
                        mainHandler.post { callBack?.onError(error, TaskManagerStatusEnum.COMPLETE) }
                    } else {
                        mainHandler.post { callBack?.onComplete() }
                    }
                }
            } else {
                status.getAndSet(5)
                mainHandler.post { callBack?.onError(error, TaskManagerStatusEnum.NETError) }
            }
        }
    }

    override fun onCancelAllTask(statusEnum: TaskManagerStatusEnum, error: String) {
        if (null != callBack) {
            mainHandler.post { callBack?.onError(error, statusEnum) }
        }
        if (statusEnum === TaskManagerStatusEnum.CANCEL) {
            cancelTasks()
        }
    }

    init {
        threadPool = Executors.newFixedThreadPool(1)
    }
}