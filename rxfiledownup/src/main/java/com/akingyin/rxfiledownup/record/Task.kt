/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.record

import com.akingyin.rxfiledownup.DEFAULT_SAVE_PATH
import com.akingyin.rxfiledownup.util.getFileNameFromUrl

/**
 * 基础任务
 * @ Description:
 * @author king
 * @ Date 2020/12/10 11:49
 * @version V1.0
 */
open class Task( var url: String,
            var taskName: String = getFileNameFromUrl(url),
            var fileName: String = "",
            var filePath: String = DEFAULT_SAVE_PATH,
            var extraInfo: String = "") {

    /**
     * Each task with unique tag.
     */
    open fun tag() = url

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true

        return if (other is Task) {
            tag() == other.tag()
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return tag().hashCode()
    }

    open fun isEmpty(): Boolean {
        return taskName.isEmpty() || fileName.isEmpty() || filePath.isEmpty()
    }
}