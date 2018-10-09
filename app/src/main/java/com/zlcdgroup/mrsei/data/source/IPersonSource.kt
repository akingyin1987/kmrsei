package com.zlcdgroup.mrsei.data.source

import com.akingyin.base.call.ApiCallBack
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 14:41
 * @version V1.0
 */
interface IPersonSource {

    /**
     * 获取所有用户信息
     */
    fun   getAllPersons():List<PersonEntity>

    /**
     * 删除超过两个月的用户信息
     */
    fun   delectOutTowMonth()

    /**
     * 保存用户信息
     */
    fun       savePerson (  personEntity: PersonEntity)

    /**
     * 删除用户信息
     */
    fun     delectPerson( personEntity: PersonEntity)

    /**
     * 获取最后一次登陆用户信息
     */
    fun     getLastPerson():PersonEntity?

    /**
     * 登录
     */
    fun    login(name:String,pass:String,callBack: ApiCallBack<LoginResultModel>)

    /**
     * 登出
     */
    fun    logOut()

    fun cancelSubscribe()
}