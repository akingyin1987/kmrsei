/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.glide.progress

import android.text.TextUtils
import com.akingyin.media.glide.OnProgressListener
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*
import kotlin.collections.HashMap


/**
 * @ Description:
 * @author king
 * @ Date 2020/6/27 17:19
 * @version V1.0
 */
object  ProgressManager {

    private val listenersMap: MutableMap<String, OnProgressListener> = Collections.synchronizedMap(HashMap())


     private var okHttpClient: OkHttpClient? = null


     @JvmStatic
    fun getGlideOkHttpClient(): OkHttpClient {
        okHttpClient = okHttpClient?:  OkHttpClient.Builder()
                .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val request: Request = chain.request()
                    val response: Response = chain.proceed(request)
                    response.body?.let {
                        responseBody ->
                        response.newBuilder()
                                .body(ProgressResponseBody(request.url.toString(), LISTENER, responseBody))
                                .build()
                    }?:response

                })
                .build()
        return okHttpClient!!
    }



    private val LISTENER: ProgressResponseBody.InternalProgressListener = object : ProgressResponseBody.InternalProgressListener {
        override fun onProgress(url: String, bytesRead: Long, totalBytes: Long) {
            val onProgressListener = getProgressListener(url)
            if (onProgressListener != null) {
                val percentage = (bytesRead * 1f / totalBytes * 100f).toInt()
                val isComplete = percentage >= 100
                onProgressListener.onProgress(isComplete, percentage, bytesRead, totalBytes)
                if (isComplete) {
                    removeListener(url)
                }
            }
        }


    }

    fun addListener(url: String, listener: OnProgressListener?) {
        if (url.isNotEmpty() && listener != null) {
            listenersMap[url] = listener
            listener.onProgress(false, 1, 0, 0)
        }
    }

    fun removeListener(url: String) {
        if (url.isNotEmpty()) {
            listenersMap.remove(url)
        }
    }

    fun getProgressListener(url: String): OnProgressListener? {
        return if (TextUtils.isEmpty(url) || listenersMap.isEmpty()) {
            null
        } else listenersMap[url]
    }
}