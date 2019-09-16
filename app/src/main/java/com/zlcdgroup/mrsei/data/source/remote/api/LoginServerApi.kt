package com.zlcdgroup.mrsei.data.source.remote.api

import com.akingyin.base.net.mode.ApiResult
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 15:20
 * @version V1.0
 */
interface LoginServerApi {

    /**
     * 登录
     */

    @FormUrlEncoded
    @POST("login")
    fun   login(@Field("data")data:String,@Field("token")token:String):Observable<ApiResult<LoginResultModel>>


    @FormUrlEncoded
    @POST("login")
    fun    loginK(@Field("data")data:String,@Field("token") token:String):Deferred<ApiResult<LoginResultModel>>
    /**
     * 登出
     */
    fun   logOut(@Field("data")data:String,@Field("token")token:String):Observable<String>

    /**
     * 下载文件
     */
    @GET
    fun   downloadFile(@Url url:String):Observable<ResponseBody>
}