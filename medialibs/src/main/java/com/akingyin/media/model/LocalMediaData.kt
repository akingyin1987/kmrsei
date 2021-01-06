/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.model


import android.os.Parcel
import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/15 12:10
 * @version V1.0
 */
 open class LocalMediaData() : MultiItemEntity, Parcelable {



    /**
     * 多媒体类型
     */

    var   mediaType = 0

    /**
     * 文本类型
     */
    var  mediaText = ""

    /**
     * 本地文件路径
     */
    var  mediaLocalPath =""

   /**
    * 音频或视频长度
    */
   var mediaDuration = 0L

    /**
     * 服务器路径
     */
    var  mediaServerPath =""


    /**
     * 原始路径
     */
    var  mediaOriginalPath =""


    /**
     * 顺序
     */
    var  mediaSort = 0

    /**
     * 是否被选择
     */
    var  mediaSelected = false


    var  mediaChecked = false

    /**
     * 是否被删除
     */
    var  mediaDelect = false



    override val itemType: Int
        get() = mediaType

    constructor(parcel: Parcel) : this() {
        mediaType = parcel.readInt()
        mediaText = parcel.readString()?:""
        mediaLocalPath = parcel.readString()?:""
        mediaDuration = parcel.readLong()
        mediaServerPath = parcel.readString()?:""
        mediaOriginalPath = parcel.readString()?:""
        mediaSort = parcel.readInt()
        mediaSelected = parcel.readByte() != 0.toByte()
        mediaChecked = parcel.readByte() != 0.toByte()
        mediaDelect = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mediaType)
        parcel.writeString(mediaText)
        parcel.writeString(mediaLocalPath)
        parcel.writeLong(mediaDuration)
        parcel.writeString(mediaServerPath)
        parcel.writeString(mediaOriginalPath)
        parcel.writeInt(mediaSort)
        parcel.writeByte(if (mediaSelected) 1 else 0)
        parcel.writeByte(if (mediaChecked) 1 else 0)
        parcel.writeByte(if (mediaDelect) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalMediaData> {
        override fun createFromParcel(parcel: Parcel): LocalMediaData {
            return LocalMediaData(parcel)
        }

        override fun newArray(size: Int): Array<LocalMediaData?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}