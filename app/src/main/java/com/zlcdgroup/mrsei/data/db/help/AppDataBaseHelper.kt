package com.zlcdgroup.mrsei.data.db.help

import android.content.Context
import androidx.room.Room

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 14:02
 * @version V1.0
 */
class AppDataBaseHelper constructor(context: Context){

    val appDataBase = Room.databaseBuilder(context, AppDatabase::class.java,
            "test-db").build()

    companion object{
        @Volatile
        var INSTANCE: AppDataBaseHelper? = null


        fun  getAppDataBase():AppDatabase?{
            return INSTANCE?.appDataBase
        }

        fun getInstance(context: Context): AppDataBaseHelper {
            if (INSTANCE == null) {
                synchronized(AppDataBaseHelper::class) {
                    if (INSTANCE == null) {
                        INSTANCE = AppDataBaseHelper(context.applicationContext)
                    }
                }
            }
            return INSTANCE!!
        }
    }

}