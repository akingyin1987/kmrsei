/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media

import android.content.Context
import android.text.TextUtils
import androidx.annotation.IntDef
import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 11:54
 * @version V1.0
 */
object MediaMimeType {

    @MediaType
    fun ofAll(): Int {

        return MediaConfig.TYPE_ALL
    }
    @MediaType
    fun ofImage(): Int {
        return MediaConfig.TYPE_IMAGE
    }
    @MediaType
    fun ofVideo(): Int {
        return MediaConfig.TYPE_VIDEO
    }

    // 表示开启Doc文档
    @MustBeDocumented
    //限定为 参数
    @IntDef(value = [MediaConfig.TYPE_ALL,MediaConfig.TYPE_IMAGE,MediaConfig.TYPE_VIDEO,
        MediaConfig.TYPE_AUDIO,MediaConfig.TYPE_TEXT])
    //表示注解作用范围，参数注解，成员注解，方法注解
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    //表示注解所存活的时间,在运行时,而不会存在 .class 文件中
    @Retention(AnnotationRetention.SOURCE)
    annotation class MediaType
    /**
     * # No longer maintain audio related functions,
     * but can continue to use but there will be phone compatibility issues
     *
     *
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    @MediaType
    fun ofAudio(): Int {
        return MediaConfig.TYPE_AUDIO
    }
    @MediaType
    fun  ofText():Int{
        return MediaConfig.TYPE_TEXT
    }


    fun ofPNG(): String {
        return MIME_TYPE_PNG
    }

    fun ofJPEG(): String{
        return MIME_TYPE_JPEG
    }

    fun ofBMP(): String {
        return MIME_TYPE_BMP
    }

    fun ofGIF(): String {
        return MIME_TYPE_GIF
    }

    fun ofWEBP(): String{
        return MIME_TYPE_WEBP
    }

    fun of3GP(): String {
        return MIME_TYPE_3GP
    }

    fun ofMP4(): String {
        return MIME_TYPE_MP4
    }

    fun ofMPEG(): String {
        return MIME_TYPE_MPEG
    }

    fun ofAVI(): String {
        return MIME_TYPE_AVI
    }

    private val MIME_TYPE_PNG = "image/png"
    private val MIME_TYPE_JPEG = "image/jpeg"
    private val MIME_TYPE_JPG = "image/jpg"
    private val MIME_TYPE_BMP = "image/bmp"
    private val MIME_TYPE_GIF = "image/gif"
    private val MIME_TYPE_WEBP = "image/webp"

    private val MIME_TYPE_3GP = "video/3gp"
    private val MIME_TYPE_MP4 = "video/mp4"
    private val MIME_TYPE_MPEG = "video/mpeg"
    private val MIME_TYPE_AVI = "video/avi"


    /**
     * isGif
     *
     * @param mimeType
     * @return
     */
    fun isGif(mimeType: String?): Boolean {
        return mimeType != null && (mimeType == "image/gif" || mimeType == "image/GIF")
    }


    /**
     * isVideo
     *
     * @param mimeType
     * @return
     */
    fun isHasVideo(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)
    }

    /**
     * isVideo
     *
     * @param url
     * @return
     */
    fun isUrlHasVideo(url: String): Boolean {
        return url.endsWith(".mp4")
    }

    /**
     * isAudio
     *
     * @param mimeType
     * @return
     */
    fun isHasAudio(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)
    }

    /**
     * isImage
     *
     * @param mimeType
     * @return
     */
    fun isHasImage(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE)
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file mimeType
     */
    fun isJPEG(mimeType: String): Boolean {
        return if (TextUtils.isEmpty(mimeType)) {
            false
        } else mimeType.startsWith(MIME_TYPE_JPEG) || mimeType.startsWith(MIME_TYPE_JPG)
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file mimeType
     */
    fun isJPG(mimeType: String): Boolean {
        return if (TextUtils.isEmpty(mimeType)) {
            false
        } else mimeType.startsWith(MIME_TYPE_JPG)
    }


    /**
     * is Network image
     *
     * @param path
     * @return
     */
    fun isHasHttp(path: String): Boolean {
        return if (TextUtils.isEmpty(path)) {
            false
        } else path.startsWith("http")
                || path.startsWith("https")
                || path.startsWith("/http")
                || path.startsWith("/https")
    }

    /**
     * Determine whether the file type is an image or a video
     *
     * @param cameraMimeType
     * @return
     */
    fun getMimeType(cameraMimeType: Int): String? {
        return when (cameraMimeType) {
            MediaConfig.TYPE_VIDEO -> MIME_TYPE_VIDEO
            MediaConfig.TYPE_AUDIO -> MIME_TYPE_AUDIO
            else -> MIME_TYPE_IMAGE
        }
    }

    /**
     * 判断文件类型是图片还是视频
     *
     * @param file
     * @return
     */
    fun getMimeType(file: File?): String? {
        if (file != null) {
            val name = file.name
            if (name.endsWith(".mp4") || name.endsWith(".avi")
                    || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.endsWith(".mov")) {
                return MIME_TYPE_VIDEO
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                    || name.endsWith(".bmp")) {
                return MIME_TYPE_IMAGE
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac") || name.endsWith(".lamr")) {
                return MIME_TYPE_AUDIO
            }
        }
        return MIME_TYPE_IMAGE
    }

    /**
     * Determines if the file name is a picture
     *
     * @param name
     * @return
     */
    fun isSuffixOfImage(name: String): Boolean {
        return (!TextUtils.isEmpty(name) && name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                || name.endsWith(".bmp"))
    }

    /**
     * Is it the same type
     *
     * @param oldMimeType
     * @param newMimeType
     * @return
     */
    fun isMimeTypeSame(oldMimeType: String, newMimeType: String): Boolean {
        return getMimeType(oldMimeType) == getMimeType(newMimeType)
    }

    /**
     * Get Image mimeType
     *
     * @param path
     * @return
     */
    fun getImageMimeType(path: String?): String? {
        return try {
            path?.let {
                val file = File(path)
                val fileName = file.name
                val last = fileName.lastIndexOf(".") + 1
                val temp = fileName.substring(last)
                "image/$temp"
            }?:MIME_TYPE_IMAGE

        } catch (e: Exception) {
            e.printStackTrace()
            MIME_TYPE_IMAGE
        }
    }


    /**
     * Picture or video
     *
     * @return
     */
    fun getMimeType(mimeType: String): Int {
        if (TextUtils.isEmpty(mimeType)) {
            return MediaConfig.TYPE_IMAGE
        }
        return if (mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)) {
            MediaConfig.TYPE_VIDEO
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)) {
            MediaConfig.TYPE_AUDIO
        } else {
            MediaConfig.TYPE_IMAGE
        }
    }

    /**
     * Get image suffix
     *
     * @param mineType
     * @return
     */
    fun getLastImgSuffix(mineType: String): String? {
        val defaultSuffix = PNG
        try {
            val index = mineType.lastIndexOf("/") + 1
            if (index > 0) {
                return "." + mineType.substring(index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return defaultSuffix
        }
        return defaultSuffix
    }


    /**
     * is content://
     *
     * @param url
     * @return
     */
    fun isContent(url: String): Boolean {
        return if (TextUtils.isEmpty(url)) {
            false
        } else url.startsWith("content://")
    }

    /**
     * Returns an error message by type
     *
     * @param context
     * @param mimeType
     * @return
     */
    fun s(context: Context, mimeType: String): String {
        val ctx = context.applicationContext
        return when {
            isHasVideo(mimeType) -> {
                ctx.getString(R.string.picture_video_error)
            }
            isHasAudio(mimeType) -> {
                ctx.getString(R.string.picture_audio_error)
            }
            else -> {
                ctx.getString(R.string.picture_error)
            }
        }
    }

    val JPEG = ".jpg"

    private val PNG = ".png"

    val MP4 = ".mp4"

    val JPEG_Q = "image/jpeg"

    val PNG_Q = "image/png"

    val MP4_Q = "video/mp4"

    val AVI_Q = "video/avi"

    val DCIM = "DCIM/Camera"

    val CAMERA = "Camera"

    val MIME_TYPE_IMAGE = "image/jpeg"
    val MIME_TYPE_VIDEO = "video/mp4"
    val MIME_TYPE_AUDIO = "audio/mpeg"


    private val MIME_TYPE_PREFIX_IMAGE = "image"
    private val MIME_TYPE_PREFIX_VIDEO = "video"
    private val MIME_TYPE_PREFIX_AUDIO = "audio"

}