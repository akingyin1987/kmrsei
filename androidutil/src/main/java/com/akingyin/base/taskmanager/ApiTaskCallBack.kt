/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager

import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum

/**
 * 任务管理返回接口
 * Created by Administrator on 2016/7/3.
 */
interface ApiTaskCallBack {
    /**
     * //返回结果
     * @param total
     * @param progress
     * @param error
     */
    fun onCallBack(total: Int, progress: Int, error: Int)

    /**
     * 任务正常完成时回调
     */
    fun onComplete()

    /**
     * 任务出错时回调
     * @param message
     * @param statusEnum
     */
    fun onError(message: String, statusEnum: TaskManagerStatusEnum)
}