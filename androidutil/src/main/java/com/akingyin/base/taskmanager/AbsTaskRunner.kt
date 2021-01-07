/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager

import android.text.TextUtils
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum
import com.akingyin.base.taskmanager.enums.TaskStatusEnum
import com.blankj.utilcode.util.StringUtils
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

/**
 * 执行的任务
 *
 * @author king
 * @date 2016/7/3
 */
abstract class AbsTaskRunner : Runnable, ApiSonTaskCallBack {
    /** 任务唯一ID标识  */
    val tag = UUID.randomUUID().toString()

    /** 基础信息错误信息时使用  */
    var baseInfo: String = ""
    var semaphore: Semaphore? = null
    var taskStatusEnum = TaskStatusEnum.NULL

    /** 子集任务状态  */
    var sonTaskStatusEnum = TaskStatusEnum.NULL

    /** 监听子集结果异步  */
    var sonTaskCallBack: ApiSonTaskCallBack? = null

    /** 子任务只需要依次执行与当前任务在同一线程  */
    val queueTasks = LinkedBlockingQueue<AbsTaskRunner>()
    fun addTask(taskRunner: AbsTaskRunner) {
        queueTasks.offer(taskRunner)
    }

    fun addTasks(taskRunners: List<AbsTaskRunner>?) {
        queueTasks.addAll(taskRunners!!)
    }

    var callBack: ITaskResultCallBack? = null

    /** 错误信息描述  */
    private var errorMsg: String=""
    fun getErrorMsg(): String {
        return errorMsg
    }

    fun setErrorMsg(errorMsg: String) {
        this.errorMsg = errorMsg
        if (!StringUtils.isEmpty(baseInfo)) {
            this.errorMsg = "$baseInfo $errorMsg"
        }
    }

    fun TaskDoing() {
        taskStatusEnum = TaskStatusEnum.DOING
    }

    fun TaskError() {
        taskStatusEnum = TaskStatusEnum.ERROR
        onComplete()
        callBack?.onCallBack(taskStatusEnum,errorMsg)
        sonTaskCallBack?.call(taskStatusEnum,this)

    }

    fun TaskOnNetError() {
        taskStatusEnum = TaskStatusEnum.NETERROR
        if (TextUtils.isEmpty(errorMsg)) {
            errorMsg = "网络错误，请稍候再试"
        }
        callBack?.onCallBack(taskStatusEnum,errorMsg)
        sonTaskCallBack?.call(taskStatusEnum,this)
    }

    fun TaskOnDoing() {
        if (taskStatusEnum !== TaskStatusEnum.CANCEL) {
            taskStatusEnum = TaskStatusEnum.DOING
        }
    }

    /**
     * 关闭整 个任务管理器
     */
    fun closeTaskManager() {
        callBack?.onCancelAllTask(TaskManagerStatusEnum.CANCEL,getErrorMsg())

    }

    /**
     * 完成整个任务管理器
     */
    fun completeTaskManager() {
        callBack?.onCancelAllTask(TaskManagerStatusEnum.COMPLETE,getErrorMsg())

    }

    fun TaskOnSuccess() {
        onComplete()
        taskStatusEnum = TaskStatusEnum.SUCCESS
        callBack?.onCallBack(taskStatusEnum,errorMsg)
        sonTaskCallBack?.call(taskStatusEnum,this)
    }

    /**
     * 取消任务
     */
    fun onCancel() {
        for (taskRunner in queueTasks) {
            taskRunner.onCancel()
        }
        queueTasks.clear()
        if (taskStatusEnum === TaskStatusEnum.NULL || taskStatusEnum === TaskStatusEnum.WAITING || taskStatusEnum === TaskStatusEnum.DOING) {
            taskStatusEnum = TaskStatusEnum.CANCEL
        }
    }

    /**
     * 任务执行前处理
     * @return TaskStatusEnum
     */
    abstract fun onBefore(): TaskStatusEnum

    /** 执行当前任务  */
    abstract fun onToDo()

    /**
     * 任务完成onComplete
     */
    protected fun onComplete() {}
    private fun doBackground() {
        try {
            TaskOnDoing()
            val temp = onBefore()
            if (taskStatusEnum === TaskStatusEnum.SUCCESS) {
                return
            }
            if (temp === TaskStatusEnum.SUCCESS) {
                doSonTaskBackground()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setErrorMsg("出错了" + e.message)
            TaskError()
        }
    }

    override fun run() {
        try {

            if (taskStatusEnum === TaskStatusEnum.CANCEL) {
                return
            }
            doBackground()
        } catch (e: Exception) {
            e.printStackTrace()
            onExceptionBack(e)
        }
    }

    private fun doSonTaskBackground() {
        if (queueTasks.size == 0) {
            sonTaskStatusEnum = TaskStatusEnum.SUCCESS
            //当子任务执行完
            onToDo()
            return
        }
        if (taskStatusEnum === TaskStatusEnum.CANCEL) {
            sonTaskStatusEnum = TaskStatusEnum.CANCEL
            return
        }

        //执行子任务
        val absTaskRunner = queueTasks.poll()
        if (null != absTaskRunner && absTaskRunner.taskStatusEnum !== TaskStatusEnum.CANCEL) {
            absTaskRunner.sonTaskCallBack = this
            absTaskRunner.doBackground()
        }
    }

    override fun call(taskStatusEnum: TaskStatusEnum, taskRunner: AbsTaskRunner) {
        sonTaskStatusEnum = taskStatusEnum
        setErrorMsg(taskRunner.getErrorMsg())
        if (taskStatusEnum === TaskStatusEnum.SUCCESS) {
            doSonTaskBackground()
        } else if (taskStatusEnum === TaskStatusEnum.ERROR ||
                taskStatusEnum === TaskStatusEnum.WAITING) {
            TaskError()
        } else if (taskStatusEnum === TaskStatusEnum.NETERROR) {
            TaskOnNetError()
        }
    }

    fun onExceptionBack(throwable: Throwable) {
        when (throwable) {
            is SocketTimeoutException -> {
                setErrorMsg("连接服务器超时，请稍候再试!")
                TaskOnNetError()
            }
            is ConnectException -> {
                setErrorMsg("网络连接错误，请检查网络是否正常或稍候重试!")
                TaskOnNetError()
            }
            is UnknownHostException -> {
                setErrorMsg("连接服务器失败，请检查连接地址是否正确或服务器是否正常开启！")
                TaskOnNetError()
            }
            is HttpException -> {
                setErrorMsg("连接服务器失败！" + throwable.message)
                TaskOnNetError()
            }
            else -> {
                setErrorMsg("出错了" + throwable.message)
                TaskError()
            }
        }
    }
}