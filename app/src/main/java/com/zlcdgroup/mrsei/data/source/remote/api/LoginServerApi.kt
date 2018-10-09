package com.zlcdgroup.mrsei.data.source.remote.api

import com.akingyin.base.net.mode.ApiResult
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import io.reactivex.Observable
import retrofit2.http.Field

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
    fun   login(@Field("data")data:String,@Field("token")token:String):Observable<ApiResult<LoginResultModel>>

    /**
     * 登出
     */
    fun   logOut(@Field("data")data:String,@Field("token")token:String):Observable<String>
}