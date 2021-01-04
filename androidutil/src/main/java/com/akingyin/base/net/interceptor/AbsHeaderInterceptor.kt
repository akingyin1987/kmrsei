/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * okhttp 栏截器
 * @property newToken String
 */
abstract class AbsHeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = getRequestHeader(request)
        val response = chain.proceed(request)

        //根据和服务端的约定判断token过期
        if (isTokenExpired(response)) {

            //同步请求方式，获取最新的Token
            val newSession = newToken
            //使用新的Token，创建新的请求
            request = getNewTokenRequestHeader(request, newSession)
            //重新请求
            return chain.proceed(request)
        }
        return decryptResponse(response)
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    abstract fun isTokenExpired(response: Response?): Boolean

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    val newToken: String
        get() = UUID.randomUUID().toString()

    /**
     * 获取头文件
     * @param request
     * @return
     */
    protected abstract fun getRequestHeader(request: Request): Request

    /**
     * 获取新的token
     * @param request
     * @param token
     * @return
     */
    protected abstract fun getNewTokenRequestHeader(request: Request, token: String?): Request

    /**
     * 解析数据
     * @param response
     * @return
     */
    protected abstract fun decryptResponse(response: Response): Response
}