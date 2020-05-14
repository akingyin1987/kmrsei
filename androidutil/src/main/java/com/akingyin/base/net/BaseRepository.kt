package com.akingyin.base.net

import android.util.Log
import com.akingyin.base.net.exception.ApiException
import com.akingyin.base.net.mode.ApiCode
import com.akingyin.base.net.mode.ApiResult
import retrofit2.Response

/**
 * @ Description:
 * @author king
 * @ Date 2020/1/15 13:44
 * @version V1.0
 */
open class BaseRepository {



        suspend fun <T : Any> safeApiCall(call: suspend () -> ApiResult<T>, errorMessage: String): T? {

            val result : Result<T> = safeApiResult(call,errorMessage)
            var data : T? = null

            when(result) {
                is Result.Success ->
                    data = result.data
                is Result.Error -> {
                    Log.d("1.DataRepository", "$errorMessage & Exception - ${result.exception}")
                }
            }


            return data

        }

        private suspend fun <T: Any> safeApiResult(call: suspend ()-> ApiResult<T>, errorMessage: String) : Result<T>{
            val response = call.invoke()
            if(response.code == ApiCode.Response.HTTP_SUCCESS) return Result.Success(response.data,response.time)

            return Result.Error(ApiException("Error Occurred during getting safe Api result, Custom ERROR - $errorMessage"))
        }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>) : Result<T>{
        val response = call.invoke()
        if(response.isSuccessful){
            response.body()?.let {
                return Result.Success(it,0)
            }


        }

        return Result.Error(ApiException("Error Occurred during getting safe Api result, Custom ERROR - ${response.message()}"))
    }



}