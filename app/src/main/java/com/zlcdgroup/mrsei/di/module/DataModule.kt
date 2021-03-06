package com.zlcdgroup.mrsei.di.module

import android.content.Context
import com.zlcdgroup.mrsei.data.db.dao.DaoSession
import com.zlcdgroup.mrsei.data.db.dao.NoticeDao
import com.zlcdgroup.mrsei.data.db.dao.PersonEntityDao
import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.data.db.help.AppDataBaseHelper
import com.zlcdgroup.mrsei.data.db.help.DbCore
import com.zlcdgroup.mrsei.data.source.local.PersonLocalSource
import com.zlcdgroup.mrsei.data.source.local.UserLocalSource
import com.zlcdgroup.mrsei.data.source.remote.PersonRemoteSource
import com.zlcdgroup.mrsei.data.source.remote.UserRemoteSource
import com.zlcdgroup.mrsei.di.qualifier.Local
import com.zlcdgroup.mrsei.di.qualifier.Remote
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 11:23
 * @version V1.0
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Module(includes = [DataModule ::class])
    @InstallIn(SingletonComponent::class)
    object DataProvidesModule {

        @Singleton
        @Provides
        fun  getDaoSession(): DaoSession {
            return   DbCore.getDaoSession()
        }

        @Singleton
        @Provides
        fun  getAppDataBaseHelp( @ApplicationContext context: Context): AppDataBaseHelper {
            return  AppDataBaseHelper.getInstance(context)
        }

        @Singleton
        @Provides
        fun   getNoticeDao(appDataBaseHelper: AppDataBaseHelper): NoticeDao {
            return  appDataBaseHelper.appDataBase.getNoticeDao()
        }

        @Singleton
        @Provides
        fun  getUserDao(daoSession: DaoSession): UserEntityDao {
            return daoSession.userEntityDao
        }

        @Singleton
        @Provides
        fun getPersonDao(daoSession: DaoSession):PersonEntityDao{
            return  daoSession.personEntityDao
        }



    }


    @Local
    @Singleton
    @Binds
    abstract  fun  providePersonLocalDataSource(personLocalSource: PersonLocalSource):PersonLocalSource


    @Remote
    @Singleton
    @Binds
    abstract  fun providePersonRemoteDataSource(personRemoteSource: PersonRemoteSource):PersonRemoteSource

    @Local
    @Singleton
    @Binds
    abstract fun provideUserLocalDataSource(userLocalSource: UserLocalSource):UserLocalSource


    @Remote
    @Singleton
    @Binds
    abstract fun provideUserRemoteDataSource(userRemoteSource: UserRemoteSource):UserRemoteSource



}