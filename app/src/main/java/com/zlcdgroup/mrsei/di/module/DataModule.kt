package com.zlcdgroup.mrsei.di.module

import com.zlcdgroup.mrsei.data.db.dao.DaoSession
import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.data.db.help.DbCore
import com.zlcdgroup.mrsei.data.source.local.UserLocalSource
import com.zlcdgroup.mrsei.data.source.remote.UserRemoteSource
import com.zlcdgroup.mrsei.di.qualifier.Local
import com.zlcdgroup.mrsei.di.qualifier.Remote
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 11:23
 * @version V1.0
 */

@Module
abstract class DataModule {



    @Module(includes = arrayOf(DataModule ::class))
    class DataProvidesModule {
        @Singleton
        @Provides
        fun  getDaoSession(): DaoSession {
            return   DbCore.getDaoSession()
        }

        @Singleton
        @Provides
        fun  getUserDao(daoSession: DaoSession): UserEntityDao {
            return daoSession.userEntityDao
        }
    }


    @Local
    @Singleton
    @Binds
    abstract fun provideUserLocalDataSource(userLocalSource: UserLocalSource):UserLocalSource


    @Remote
    @Singleton
    @Binds
    abstract fun provideUserRemoteDataSource(userRemoteSource: UserRemoteSource):UserRemoteSource



}