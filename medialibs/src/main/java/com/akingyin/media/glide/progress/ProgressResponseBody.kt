/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.glide.progress

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/27 17:26
 * @version V1.0
 */
class ProgressResponseBody constructor(var url: String, var internalProgressListener: InternalProgressListener?, var responseBody: ResponseBody) : ResponseBody() {
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())


    private var bufferedSource: BufferedSource? = null



    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {

            bufferedSource = source(responseBody.source())?.buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source? {
        return object : ForwardingSource(source) {
            var totalBytesRead: Long = 0
            var lastTotalBytesRead: Long = 0

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead == -1L) 0 else bytesRead
                internalProgressListener?.let {
                    if (lastTotalBytesRead != totalBytesRead) {
                        lastTotalBytesRead = totalBytesRead
                        mainThreadHandler.post { it.onProgress(url, totalBytesRead, contentLength()) }
                    }
                }

                return bytesRead
            }
        }
    }

    interface InternalProgressListener {
        fun onProgress(url: String, bytesRead: Long, totalBytes: Long)
    }
}