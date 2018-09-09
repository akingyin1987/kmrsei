package com.zlcdgroup.mrsei.data.source.remote

import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.data.source.IUserSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/5 18:11
 * @version V1.0
 */

@Singleton
class UserRemoteSource @Inject constructor():IUserSource{

    override fun getUserList(): List<UserEntity>? {
       return  null
    }

    override fun getUserById(id: Long): UserEntity? {
        return  null
    }

    override fun addUser(userEntity: UserEntity): Boolean {
        return false
    }

    override fun delectUser(userEntity: UserEntity): Boolean {
        return   false
    }

    override fun modeiyUser(userEntity: UserEntity): Boolean {
        return  false
    }
}