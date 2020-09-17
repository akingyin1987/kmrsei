/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */
package com.akingyin.media.camera

import android.graphics.Point
import android.os.Parcel
import android.os.Parcelable
import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.utils.StringUtils
import com.akingyin.media.camera.CameraManager.*
import java.io.File


/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/7/17 12:25
 */


class CameraParameBuild() : Parcelable {
    @CameraFlashModel
    var flashModel: Int = CameraFlashModel.CAMERA_FLASH_NONE

    @CameraShutterSound
    var shutterSound: Int = CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF

    @CameraNetGrid
    var netGrid: Int = CameraNetGrid.CAMERA_NET_GRID_NONE

    /** true = 手动区域对焦  fase = 点击自动对焦*/
    var  supportManualFocus = true

    /** 是否技能运动对焦 */
    var  supportMoveFocus = false

    /** 支持定位 */
    var  supportLocation = true

    /** 是否支持对焦成功后自动拍照 */
    var  supportFocesedAutoPhoto = true

    /** 对焦后自动拍照延迟时间 */
    var  focesedAutoPhotoDelayTime = 2

    /** 是否支持拍照后自动保存数据 */
    var  supportAutoSavePhoto = true

    /** 拍照后自动保存时间*/
    var  autoSavePhotoDelayTime = 2

    /** 音量键控制 0= 无 1=拍照 2=预览缩放 */
    var  volumeKeyControl = 0

    /** 是否支持拍多张照片 */
    var  supportMultiplePhoto = false

    /**
     * 设置相机分辨率
     */
    var cameraResolution: Point? = null

    /**
     * 当前图片保存路径
     */
    var  localPath =AppFileConfig.APP_FILE_ROOT+ File.separator+StringUtils.getUUID()+".jpg"

    /**
     * 是否强制横向照片
     */
    var  horizontalPicture = false

    /**
     * 相机的角度
     */
    var  cameraAngle = 90

    /**
     * 经纬度及坐标类型
     */
    var  lat = 0.0
    var  lng = 0.0

    @LocalType
    var  locType=LocalType.CORR_TYPE_BD09

    constructor(parcel: Parcel) : this() {
        flashModel = parcel.readInt()
        shutterSound = parcel.readInt()
        netGrid = parcel.readInt()
        supportManualFocus = parcel.readByte() != 0.toByte()
        supportMoveFocus = parcel.readByte() != 0.toByte()
        supportLocation = parcel.readByte() != 0.toByte()
        supportFocesedAutoPhoto = parcel.readByte() != 0.toByte()
        focesedAutoPhotoDelayTime = parcel.readInt()
        supportAutoSavePhoto = parcel.readByte() != 0.toByte()
        autoSavePhotoDelayTime = parcel.readInt()
        volumeKeyControl = parcel.readInt()
        supportMultiplePhoto = parcel.readByte() != 0.toByte()
        cameraResolution = parcel.readParcelable(Point::class.java.classLoader)
        localPath = parcel.readString()?:""
        horizontalPicture = parcel.readByte() != 0.toByte()
        cameraAngle = parcel.readInt()
        lat = parcel.readDouble()
        lng = parcel.readDouble()
        locType = parcel.readString()?:""
    }

    class Builder {
        @CameraFlashModel
        private var flashModel = CameraFlashModel.CAMERA_FLASH_NONE

        @CameraShutterSound
        private var shutterSound = CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE

        @CameraNetGrid
        private var netGrid = CameraNetGrid.CAMERA_NET_GRID_NONE

        private var  localPath =""

        private var  horizontalPicture = false
        private var  cameraAngle = 90

        private var cameraResolution: Point? = null


        fun build(): CameraParameBuild {
            return CameraParameBuild().apply {
                cameraResolution = this@Builder.cameraResolution
                netGrid = this@Builder.netGrid
                flashModel = this@Builder.flashModel
                shutterSound = this@Builder.shutterSound
                localPath = this@Builder.localPath
                horizontalPicture = this@Builder.horizontalPicture
                cameraAngle = this@Builder.cameraAngle
            }
        }
    }

    override fun toString(): String {
        return "CameraParameBuild(flashModel=$flashModel, shutterSound=$shutterSound, netGrid=$netGrid, supportManualFocus=$supportManualFocus, supportLocation=$supportLocation, supportFocesedAutoPhoto=$supportFocesedAutoPhoto, focesedAutoPhotoDelayTime=$focesedAutoPhotoDelayTime, supportAutoSavePhoto=$supportAutoSavePhoto, autoSavePhotoDelayTime=$autoSavePhotoDelayTime, cameraResolution=$cameraResolution, localPath='$localPath', horizontalPicture=$horizontalPicture, cameraAngle=$cameraAngle)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(flashModel)
        parcel.writeInt(shutterSound)
        parcel.writeInt(netGrid)
        parcel.writeByte(if (supportManualFocus) 1 else 0)
        parcel.writeByte(if (supportMoveFocus) 1 else 0)
        parcel.writeByte(if (supportLocation) 1 else 0)
        parcel.writeByte(if (supportFocesedAutoPhoto) 1 else 0)
        parcel.writeInt(focesedAutoPhotoDelayTime)
        parcel.writeByte(if (supportAutoSavePhoto) 1 else 0)
        parcel.writeInt(autoSavePhotoDelayTime)
        parcel.writeInt(volumeKeyControl)
        parcel.writeByte(if (supportMultiplePhoto) 1 else 0)
        parcel.writeParcelable(cameraResolution, flags)
        parcel.writeString(localPath)
        parcel.writeByte(if (horizontalPicture) 1 else 0)
        parcel.writeInt(cameraAngle)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(locType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CameraParameBuild> {
        override fun createFromParcel(parcel: Parcel): CameraParameBuild {
            return CameraParameBuild(parcel)
        }

        override fun newArray(size: Int): Array<CameraParameBuild?> {
            return arrayOfNulls(size)
        }
    }


}