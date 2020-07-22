/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */
package com.akingyin.camera

import android.graphics.Point
import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.utils.StringUtils
import com.akingyin.camera.CameraManager.*
import java.io.File
import java.io.Serializable

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/7/17 12:25
 */


class CameraParameBuild :Serializable {
    @CameraFlashModel
    var flashModel: Int = CameraFlashModel.CAMERA_FLASH_NONE

    @CameraShutterSound
    var shutterSound: Int = CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF

    @CameraNetGrid
    var netGrid: Int = CameraNetGrid.CAMERA_NET_GRID_NONE

    /** 手动区域对焦 */
    var  supportManualFocus = true

    /** 是否支持对焦成功后自动拍照 */
    var  supportFocesedAutoPhoto = true

    /** 对焦后自动拍照延迟时间 */
    var  focesedAutoPhotoDelayTime = 2

    /** 是否支持拍照后自动保存数据 */
    var  supportAutoSavePhoto = true

    /** 拍照后自动保存时间*/
    var  autoSavePhotoDelayTime = 2

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
        fun flashModel(`val`: Int): Builder {
            flashModel = `val`
            return this
        }

        fun shutterSound(`val`: Int): Builder {
            shutterSound = `val`
            return this
        }

        fun netGrid(`val`: Int): Builder {
            netGrid = `val`
            return this
        }

        fun cameraResolution(`val`: Point?): Builder {
            cameraResolution = `val`
            return this
        }

        fun  buildLocalPath(localPath:String):Builder{
            this.localPath = localPath
            return this
        }

        fun  buildHorizontalPicture(horizontalPicture:Boolean):Builder{
            this.horizontalPicture = horizontalPicture
            return  this
        }

        fun buildCameraAngle(cameraAngle:Int):Builder{
            this.cameraAngle = cameraAngle
            return  this
        }

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


}