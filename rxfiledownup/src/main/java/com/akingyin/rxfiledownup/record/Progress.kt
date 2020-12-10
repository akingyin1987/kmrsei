/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.rxfiledownup.record

import com.akingyin.rxfiledownup.util.formatSize
import com.akingyin.rxfiledownup.util.ratio

/**
 * @ Description:
 * @author king
 * @ Date 2020/12/10 11:31
 * @version V1.0
 */
class Progress( var downloadSize: Long = 0,
                var totalSize: Long = 0,
                /**
                 * 用于标识一个链接是否是分块下载, 如果该值为true, 那么totalSize为-1
                 */
                var isChunked: Boolean = false) {

    /**
     * Return total size str. eg: 10M
     */
    fun totalSizeStr(): String {
        return totalSize.formatSize()
    }

    /**
     * Return download size str. eg: 3M
     */
    fun downloadSizeStr(): String {
        return downloadSize.formatSize()
    }

    /**
     * Return percent number.
     */
    fun percent(): Double {
        check(!isChunked) { "Chunked can not get percent!" }

        return downloadSize ratio totalSize
    }

    /**
     * Return percent string.
     */
    fun percentStr(): String {
        return "${percent()}%"
    }
}