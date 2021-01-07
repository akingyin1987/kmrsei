package com.akingyin.base.taskmanager.enums

/**
 * Created by Administrator on 2017/12/29.
 */
enum class ThreadTypeEnum(var code: Int, statusName: String) {
    MainThread(1, "主线程"), CurrentThread(2, "当前线程"), IoThread(3, "IO线程");
}