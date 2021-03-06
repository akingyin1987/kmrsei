/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera


import android.os.Parcel
import android.os.Parcelable
import com.akingyin.media.MediaMimeType

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/22 15:18
 * @version V1.0
 */
class CameraData() : Parcelable {

    @MediaMimeType.MediaType
    var  mediaType:Int = MediaMimeType.ofAll()

    var  localPath:String =""

    var  originalPath = ""

    /** 多文件目录 */
    var  dirRootPath = ""

    var  supportMultiplePhoto = false

    /** 多张照片 */
    var  cameraPhotoDatas = mutableListOf<String>()

    var   result = true

    /** 其它信息 */
    var   dataJson=""

    /**图片标签 */
    var   imageTag=""

    /** 图片提示内容 */
    var   cameraContentTips=""

    constructor(parcel: Parcel) : this() {
        mediaType = parcel.readInt()
        localPath = parcel.readString()?:""
        originalPath = parcel.readString()?:""
        dirRootPath = parcel.readString()?:""
        supportMultiplePhoto = parcel.readByte() != 0.toByte()
        result = parcel.readByte() != 0.toByte()
        dataJson = parcel.readString()?:""
        imageTag = parcel.readString()?:""
        cameraContentTips = parcel.readString()?:""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mediaType)
        parcel.writeString(localPath)
        parcel.writeString(originalPath)
        parcel.writeString(dirRootPath)
        parcel.writeByte(if (supportMultiplePhoto) 1 else 0)
        parcel.writeByte(if (result) 1 else 0)
        parcel.writeString(dataJson)
        parcel.writeString(imageTag)
        parcel.writeString(cameraContentTips)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CameraData> {
        override fun createFromParcel(parcel: Parcel): CameraData {
            return CameraData(parcel)
        }

        override fun newArray(size: Int): Array<CameraData?> {
            return arrayOfNulls(size)
        }
    }


}