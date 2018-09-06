package com.zlcdgroup.mrsei.data.source

import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.data.source.local.UserLocalSource
import com.zlcdgroup.mrsei.data.source.remote.UserRemoteSource
import com.zlcdgroup.mrsei.di.qualifier.Local
import com.zlcdgroup.mrsei.di.qualifier.Remote
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/6 16:20
 * @version V1.0
 */

@Singleton
class UserRepository @Inject constructor(@Local var userlocal :UserLocalSource
                                            ,@Remote var userRemoteSource: UserRemoteSource):IUserSource {

    override fun getUserList(): List<UserEntity>? {
       return  userlocal.getUserList()
    }

    override fun getUserById(id: Long): UserEntity? {
        return  userlocal.getUserById(id)
    }

    override fun addUser(userEntity: UserEntity): Boolean {
        return  userlocal.addUser(userEntity)
    }

    override fun delectUser(userEntity: UserEntity): Boolean {
        return  userlocal.delectUser(userEntity)
    }

    override fun modeiyUser(userEntity: UserEntity): Boolean {
        return userlocal.modeiyUser(userEntity)
    }
}