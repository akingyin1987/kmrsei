package com.zlcdgroup.mrsei.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zlcdgroup.mrsei.data.entity.NoticeEntity

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 13:43
 * @version V1.0
 */

@Dao
interface NoticeDao {

    @Query("select * from tb_notice")
    fun   findAllNotice(): LiveData<List<NoticeEntity>>

    @Delete
    fun   delectNotice(noticeEntity: NoticeEntity)

    @Insert(onConflict =OnConflictStrategy.ABORT )
    fun   insertNotice(noticeEntity: NoticeEntity)

    @Update
    fun    updateNotice(noticeEntity: NoticeEntity)

    @Insert
    fun  insertAll(notices:List<NoticeEntity>)
}