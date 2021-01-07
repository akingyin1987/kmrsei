/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager.enums

/**
 * 任务管理状态
 * Created by Administrator on 2016/7/3.
 */
enum class TaskManagerStatusEnum(var code: Int,   statusName: String) {
    NULL(1, "无"), DOING(2, "进行中"), CANCEL(3, "已取消"), COMPLETE(4, "已完成"), NETError(5, "网络错误");

    companion object {
        @JvmStatic
        fun getTaskManagerStatus(code: Int): TaskManagerStatusEnum? {
            for (c in values()) {
                if (c.code == code) {
                    return c
                }
            }
            return null
        }
    }
}