/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.manager

import com.akingyin.rxfiledownup.record.Progress

/**
 * @ Description:
 * @author king
 * @ Date 2020/12/10 11:44
 * @version V1.0
 */
fun Status.isEndStatus(): Boolean {
    return when (this) {
        is Normal -> false
        is Pending -> false
        is Started -> false
        is Downloading -> false
        is Uploading -> false
        is Paused -> true
        is Completed -> true
        is Failed -> true
        is Deleted -> true
        else -> false
    }
}

sealed class Status {
    var progress: Progress = Progress()
}

class Normal : Status()

class Pending : Status()

class Started : Status()

class Downloading : Status()

class Uploading:Status()

class Paused : Status()

class Completed : Status()

class Failed : Status() {
    var throwable: Throwable = RuntimeException("UNKNOWN ERROR")
}

class Deleted : Status()