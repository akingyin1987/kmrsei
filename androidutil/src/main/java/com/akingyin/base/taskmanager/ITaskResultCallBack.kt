/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager

import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum
import com.akingyin.base.taskmanager.enums.TaskStatusEnum

/**
 * 任务执行结果回调
 * Created by Administrator on 2016/7/3.
 */
interface ITaskResultCallBack {
    //任务状态回调
    fun onCallBack(statusEnum: TaskStatusEnum, error: String)

    //取消所有任务执行
    fun onCancelAllTask(statusEnum: TaskManagerStatusEnum, error: String)
}