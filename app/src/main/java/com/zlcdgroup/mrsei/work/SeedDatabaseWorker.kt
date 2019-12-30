package com.zlcdgroup.mrsei.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.zlcdgroup.mrsei.Constants.PLANT_DATA_FILENAME
import com.zlcdgroup.mrsei.data.db.help.AppDatabase
import com.zlcdgroup.mrsei.data.entity.NoticeEntity
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 15:33
 * @version V1.0
 */
class SeedDatabaseWorker(context: Context,
                         workerParams: WorkerParameters):CoroutineWorker(context,workerParams) {


    private val TAG by lazy { SeedDatabaseWorker::class.java.simpleName }


    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(PLANT_DATA_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val plantType = object : TypeToken<List<NoticeEntity>>() {}.type
                    val plantList: List<NoticeEntity> = Gson().fromJson(jsonReader, plantType)

                    val database = AppDatabase.getInstance(applicationContext)
                    database.getNoticeDao().insertAll(plantList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Error seeding database")
            Result.failure()
        }
    }
}