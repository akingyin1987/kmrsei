package com.zlcdgroup.mrsei.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 12:24
 * @version V1.0
 */

 @Entity(tableName = "tb_notice")
  class NoticeEntity {


    @PrimaryKey(autoGenerate = true)
    var id: Long?=null

    @ColumnInfo
    var indexOrder:Int = 0

    @ColumnInfo
    var name:String=""
    @ColumnInfo
    var demo:String=""
    @ColumnInfo
    var time:Long = 0L

    @Ignore
    var str :String=""
}
