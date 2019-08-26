package com.zlcdgroup.mrsei.data.db.help

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.zlcdgroup.mrsei.data.db.dao.NoticeDao
import com.zlcdgroup.mrsei.data.entity.NoticeEntity
import com.zlcdgroup.mrsei.work.SeedDatabaseWorker

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 13:55
 * @version V1.0
 */

@Database(entities = arrayOf(NoticeEntity::class),version = 1,exportSchema = true)
abstract class AppDatabase :RoomDatabase() {

    abstract fun   getNoticeDao():NoticeDao


        companion object {

            val DATABASE_NAME : String = "test-db"

            // For Singleton instantiation
            @Volatile private var instance: AppDatabase? = null

            fun getInstance(context: Context): AppDatabase {
                return instance ?: synchronized(this) {
                    instance ?: buildDatabase(context).also { instance = it }
                }
            }

            // Create and pre-populate the database. See this article for more details:
            // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
            private fun buildDatabase(context: Context): AppDatabase {
                return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                                WorkManager.getInstance(context).enqueue(request)
                            }
                        })

                        .build()
            }
        }

}