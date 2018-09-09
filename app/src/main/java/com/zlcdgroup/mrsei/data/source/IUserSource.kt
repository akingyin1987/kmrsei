package com.zlcdgroup.mrsei.data.source

import com.zlcdgroup.mrsei.data.entity.UserEntity

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 11:16
 * @version V1.0
 */
interface IUserSource {

    /**
     * 获取所有用户
     */
    fun   getUserList():List<UserEntity>?

    /**
     * 通过ID获取用户
     */
    fun   getUserById(id:Long):UserEntity?


    /**
     * 新增用户
     */
    fun   addUser(userEntity: UserEntity):Boolean

    /**
     * 删除用户
     */
    fun    delectUser(userEntity: UserEntity) :Boolean


    /**
     * 修改用户
     */
    fun    modeiyUser(userEntity: UserEntity):Boolean
}