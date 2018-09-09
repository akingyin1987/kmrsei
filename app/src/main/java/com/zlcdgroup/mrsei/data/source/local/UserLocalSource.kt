package com.zlcdgroup.mrsei.data.source.local

import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.data.source.IUserSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/5 18:05
 * @version V1.0
 */

@Singleton
class UserLocalSource @Inject constructor( var  userEntityDao: UserEntityDao) : IUserSource {
    override fun getUserList(): List<UserEntity> {
       return   userEntityDao.queryBuilder().list()
    }

    override fun getUserById(id: Long): UserEntity {
       return   userEntityDao.load(id)
    }

    override fun addUser(userEntity: UserEntity): Boolean {
        try {
            userEntityDao.save(userEntity)
        }catch (e :Exception){
            e.printStackTrace()
        }
        return  false
    }

    override fun delectUser(userEntity: UserEntity): Boolean {
        try {
            userEntityDao.delete(userEntity)
        }catch (e :Exception){
            e.printStackTrace()
        }
        return  false
    }

    override fun modeiyUser(userEntity: UserEntity): Boolean {
        try {
            userEntityDao.save(userEntity)
        }catch (e :Exception){
            e.printStackTrace()
        }
        return  false
    }
}