package com.akingyin.base.repo

/**
 * @ Description:
 * @author king
 * @ Date 2020/1/17 12:41
 * @version V1.0
 */
data class Resource<out T> (val status: Status, val data: T?, val message: String?){
    companion object {

        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String): Resource<T> {
            return Resource(Status.ERROR, null, msg)
        }

        fun <T> loading(msg: String): Resource<T> {
            return Resource(Status.LOADING, null, msg)
        }
    }
}