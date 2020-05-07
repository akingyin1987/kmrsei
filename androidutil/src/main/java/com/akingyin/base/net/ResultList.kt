package com.akingyin.base.net

import com.akingyin.base.net.exception.ApiException

/**
 * @ Description:
 * @author king
 * @ Date 2020/4/11 17:08
 * @version V1.0
 */
sealed class ResultList <out T:Any>{
    data class Success<out T : Any>(val data: List<T>?,val time:Long) : ResultList<T>()

    data class Error(val exception: ApiException) : ResultList<Nothing>()
}