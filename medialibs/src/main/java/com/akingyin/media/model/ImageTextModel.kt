/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.media.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * 图文 显示
 * @author king
 * @version V1.0
 * @ Description:
 *
 *
 * @ Date 2017/12/5 11:34
 */
class ImageTextModel() : Serializable, Parcelable {

    var localPath: String = ""

    var serverPath: String = ""

    /** 下载文件地址 */
    var downloadPath: String = ""

    var text: String = ""

    var title: String = ""

    /**
     * 0=文字 1=图片 2=视频 3=音频
     */

    var multimediaType = 0

    //是否连接网络获取

    var haveNetServer = false

    /** 是否被选中 */
    var checked = false


    /** 关联的外部数据ID */
    var objectId = 0L

    /** 对象类 */
    var itemType = 0

    constructor(parcel: Parcel) : this() {
        localPath = parcel.readString().toString()
        serverPath = parcel.readString().toString()
        downloadPath = parcel.readString().toString()
        text = parcel.readString().toString()
        title = parcel.readString().toString()
        multimediaType = parcel.readInt()
        haveNetServer = parcel.readByte() != 0.toByte()
        checked = parcel.readByte() != 0.toByte()
        objectId = parcel.readLong()
        itemType = parcel.readInt()
    }


    companion object {
        private const val serialVersionUID = -3037511222172328451L

        const val TEXT = 0
        const val IMAGE = 1
        const val VIDEO = 2
        const val AUDIO = 3
        fun buildModel(localPath: String, serverPath: String, downloadPath: String = "", text: String = "", title: String = "", multimediaType: Int = 1, haveNetServer: Boolean = true, objectId: Long = 0L, itemType: Int = 0): ImageTextModel {
            return ImageTextModel().apply {
                this.multimediaType = multimediaType
                this.haveNetServer = haveNetServer
                this.serverPath = serverPath
                this.title = title
                this.text = text
                this.localPath = localPath
                this.downloadPath = downloadPath
                this.itemType = itemType
                this.objectId = objectId
            }

        }

        @JvmField
        val CREATOR: Parcelable.Creator<ImageTextModel> = object : Parcelable.Creator<ImageTextModel> {
            override fun createFromParcel(parcel: Parcel): ImageTextModel {
                return ImageTextModel(parcel)
            }

            override fun newArray(size: Int): Array<ImageTextModel?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun toString(): String {
        return "ImageTextModel(localPath='$localPath', serverPath='$serverPath', text='$text', multimediaType=$multimediaType)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(localPath)
        parcel.writeString(serverPath)
        parcel.writeString(downloadPath)
        parcel.writeString(text)
        parcel.writeString(title)
        parcel.writeInt(multimediaType)
        parcel.writeByte(if (haveNetServer) 1 else 0)
        parcel.writeByte(if (checked) 1 else 0)
        parcel.writeLong(objectId)
        parcel.writeInt(itemType)
    }

    override fun describeContents(): Int {
        return 0
    }


}