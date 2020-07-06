/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.media.model

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
class ImageTextModel : Serializable {

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
    var  checked = false


    /** 关联的外部数据ID */
    var objectId = 0L


    companion object {
        private const val serialVersionUID = -3037511222172328451L

        const val TEXT = 0
        const val IMAGE = 1
        const val VIDEO = 2
        const val AUDIO = 3
        fun buildModel(localPath: String, serverPath: String, downloadPath: String = "", text: String = "", title: String = "", multimediaType: Int = 1, haveNetServer: Boolean = true): ImageTextModel {
            return ImageTextModel().apply {
                this.multimediaType = multimediaType
                this.haveNetServer = haveNetServer
                this.serverPath = serverPath
                this.title = title
                this.text = text
                this.localPath = localPath
                this.downloadPath = downloadPath
            }

        }
    }

    override fun toString(): String {
        return "ImageTextModel(localPath='$localPath', serverPath='$serverPath', text='$text', multimediaType=$multimediaType)"
    }


}