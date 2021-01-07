/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.taskmanager.enums

/**
 * //任务执行状态
 * Created by Administrator on 2016/7/3.
 */
enum class TaskStatusEnum(var code: Int,  statusName: String) {
    NULL(1, "无"), WAITING(2, "等待中"), DOING(3, "进行中"), SUCCESS(4, "成功"), ERROR(5, "错误"), NETERROR(6, "网络错误"), CANCEL(7, "已取消");

    companion object {
        fun getName(code: Int): TaskStatusEnum? {
            for (c in values()) {
                if (c.code == code) {
                    return c
                }
            }
            return null
        }
    }
}