package com.zlcdgroup.mrsei.data.source.remote.api

import com.akingyin.base.net.mode.ApiResult
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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

    /**
     * 登出
     */
    fun   logOut(@Field("data")data:String,@Field("token")token:String):Observable<String>
}