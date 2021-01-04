/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.exception

import com.akingyin.base.net.mode.ApiCode
import com.akingyin.base.net.mode.ApiListResult
import com.akingyin.base.net.mode.ApiResult
import com.alibaba.fastjson.JSONException
import retrofit2.HttpException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

/**
 *
 * @author Administrator
 * @date 2017/9/12
 */
class ApiException(var code: Int=ApiCode.Http.UNAUTHORIZED,var msg:String="",var throwable: Throwable?=null) : Exception(msg,throwable) {




    companion object {
        @JvmStatic
        fun isSuccess(apiResult: ApiResult<*>?): Boolean {
            return if (apiResult == null) {
                false
            } else apiResult.code == ApiCode.Response.HTTP_SUCCESS || ignoreSomeIssue(apiResult.code)
        }
        @JvmStatic
        fun isSuccess(apiResult: ApiListResult<*>?): Boolean {
            return if (apiResult == null) {
                false
            } else apiResult.code == ApiCode.Response.HTTP_SUCCESS || ignoreSomeIssue(apiResult.code)
        }

        private fun ignoreSomeIssue(code: Int): Boolean {
            return when (code) {
                ApiCode.Response.TIMESTAMP_ERROR, ApiCode.Response.ACCESS_TOKEN_EXPIRED, ApiCode.Response.REFRESH_TOKEN_EXPIRED, ApiCode.Response.OTHER_PHONE_LOGINED, ApiCode.Response.SIGN_ERROR -> true
                else -> false
            }
        }

        fun handleException(e: Throwable): ApiException {
            val ex: ApiException
            return when (e) {
                is HttpException -> {
                    ex = ApiException(throwable = e, code = ApiCode.Request.HTTP_ERROR)
                    when (e.code()) {
                        ApiCode.Http.UNAUTHORIZED, ApiCode.Http.FORBIDDEN, ApiCode.Http.NOT_FOUND, ApiCode.Http.REQUEST_TIMEOUT, ApiCode.Http.GATEWAY_TIMEOUT, ApiCode.Http.INTERNAL_SERVER_ERROR, ApiCode.Http.BAD_GATEWAY, ApiCode.Http.SERVICE_UNAVAILABLE -> ex.msg = "网络出错，请稍候再试"
                        else -> ex.msg = "网络出错，请稍候再试"
                    }
                    ex
                }
                is JSONException -> {
                    ex = ApiException(throwable = e,code =  ApiCode.Request.PARSE_ERROR)
                    ex.msg = "数据格式错误，请联系相关人员！"
                    ex
                }
                is ConnectException -> {
                    ex = ApiException(throwable = e, code = ApiCode.Request.NETWORK_ERROR)
                    ex.msg = "网络连接错误，请检查网络是否正常或稍候重试"
                    ex
                }
                is SSLHandshakeException -> {
                    ex = ApiException(throwable = e,code =  ApiCode.Request.SSL_ERROR)
                    ex.msg = "证书出错，请连接相关人员!"
                    ex
                }
                is SocketTimeoutException -> {
                    ex = ApiException(throwable = e, code = ApiCode.Request.TIMEOUT_ERROR)
                    ex.msg = "连接服务器超时，请稍候再试!"
                    ex
                }
                else -> {
                    ex = ApiException(throwable = e,code =  ApiCode.Request.UNKNOWN)
                    ex.msg = "UNKNOWN" + e.message
                    ex
                }
            }
        }
    }
}