package com.zlcdgroup.mrsei.viewModel

import com.akingyin.base.mvvm.viewmodel.AutoDisposeViewModel
import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.data.db.help.DbCore
import com.zlcdgroup.mrsei.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2019/12/30 12:02
 * @version V1.0
 */
class UserViewModel @Inject constructor (var userEntityDao: UserEntityDao) : AutoDisposeViewModel() {

      private  suspend fun  getUser():List<UserEntity> = withContext(Main){
          return@withContext  DbCore.getDaoSession().userEntityDao.loadAll()
      }

     private    fun   setUser(){

     }
}