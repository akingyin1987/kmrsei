package com.akingyin.base.net

import com.akingyin.base.net.exception.ApiException


/**
 * 密封 Result 类
 * 这是用来处理网络响应的类。它可能成功返回所需的数据，也可能发生异常而出错。
 * @ Description:
 * @author king
 * @ Date 2019/11/23 11:35
 * @version V1.0
 */
sealed class Result<out T:Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()

    data class Failure(val exception: Exception) : Result<Nothing>()
}