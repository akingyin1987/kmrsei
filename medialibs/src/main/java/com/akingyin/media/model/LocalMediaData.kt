/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.model


import com.akingyin.media.MediaMimeType
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/15 12:10
 * @version V1.0
 */
 open class LocalMediaData :Serializable, MultiItemEntity {

    /**
     * 多媒体类型
     */
    @MediaMimeType.MediaType
    var   mediaType = MediaMimeType.ofAll()

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

    /**
     * 换转成实体文件
      */
    fun   transformEntity(){

    }

    override val itemType: Int
        get() = if(mediaType == MediaMimeType.ofAll()){0}else{1}
}