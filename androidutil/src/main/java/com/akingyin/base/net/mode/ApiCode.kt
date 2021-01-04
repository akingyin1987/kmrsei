/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.net.mode

/**
 * @author king
 * @version V1.0
 * @ Description:
 * API状态码
 *
 * @ Date 2020/11/10 16:24
 */
class ApiCode {
    object Http {
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val REQUEST_TIMEOUT = 408
        const val INTERNAL_SERVER_ERROR = 500
        const val BAD_GATEWAY = 502
        const val SERVICE_UNAVAILABLE = 503
        const val GATEWAY_TIMEOUT = 504
        const val SUCCESS = 200 /*=======================================*/
    }

    object Request {
        /*===========Request请求码================*/ //未知错误
        const val UNKNOWN = 1000

        //解析错误
        const val PARSE_ERROR = 1001

        //网络错误
        const val NETWORK_ERROR = 1002

        //协议出错
        const val HTTP_ERROR = 1003

        //证书出错
        const val SSL_ERROR = 1005

        //连接超时
        const val TIMEOUT_ERROR = 1006

        //调用错误
        const val INVOKE_ERROR = 1007

        //类转换错误
        const val CONVERT_ERROR = 1008 /*========================================*/
    }

    object Response {
        /*===========Response响应码================*/
        /** HTTP请求成功状态码  */
        const val HTTP_SUCCESS = 200

        /** AccessToken错误或已过期  */
        const val ACCESS_TOKEN_EXPIRED = 10001

        /** RefreshToken错误或已过期  */
        const val REFRESH_TOKEN_EXPIRED = 10002

        /** 帐号在其它手机已登录  */
        const val OTHER_PHONE_LOGINED = 10003

        /** 时间戳过期  */
        const val TIMESTAMP_ERROR = 10004

        /** 缺少授权信息,没有AccessToken  */
        const val NO_ACCESS_TOKEN = 10005

        /** 签名错误  */
        const val SIGN_ERROR = 10006 /*============================================*/
    }
}