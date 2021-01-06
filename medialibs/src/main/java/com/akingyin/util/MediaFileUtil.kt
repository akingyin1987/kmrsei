/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.util

import java.util.*
import kotlin.collections.HashMap

/**
 * 判断文件的类型
 * @ Description:
 * @author king
 * @ Date 2020/8/6 17:16
 * @version V1.0
 */
object MediaFileUtil {

    var sFileExtensions: String? = null

    // Audio
    const val FILE_TYPE_MP3 = 1
    const val FILE_TYPE_M4A = 2
    const val FILE_TYPE_WAV = 3
    const val FILE_TYPE_AMR = 4
    const val FILE_TYPE_AWB = 5
    const val FILE_TYPE_WMA = 6
    const val FILE_TYPE_OGG = 7
    private const val FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3
    private const val LAST_AUDIO_FILE_TYPE = FILE_TYPE_OGG

    // MIDI
    const val FILE_TYPE_MID = 11
    const val FILE_TYPE_SMF = 12
    const val FILE_TYPE_IMY = 13
    private const val FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID
    private const val LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY

    // Video
    const val FILE_TYPE_MP4 = 21
    const val FILE_TYPE_M4V = 22
    const val FILE_TYPE_3GPP = 23
    const val FILE_TYPE_3GPP2 = 24
    const val FILE_TYPE_WMV = 25
    private const val FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4
    private const val LAST_VIDEO_FILE_TYPE = FILE_TYPE_WMV

    // Image
    const val FILE_TYPE_JPEG = 31
    const val FILE_TYPE_GIF = 32
    const val FILE_TYPE_PNG = 33
    const val FILE_TYPE_BMP = 34
    const val FILE_TYPE_WBMP = 35
    private const val FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG
    private const val LAST_IMAGE_FILE_TYPE = FILE_TYPE_WBMP

    // Playlist
    const val FILE_TYPE_M3U = 41
    const val FILE_TYPE_PLS = 42
    const val FILE_TYPE_WPL = 43
    private const val FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U
    private const val LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL

    //静态内部类
    class MediaFileType(var fileType: Int, var mimeType: String)

    private val sFileTypeMap = HashMap<String, MediaFileType>()
    private val sMimeTypeMap = HashMap<String, Int>()
    fun addFileType(extension: String, fileType: Int, mimeType: String) {
        sFileTypeMap[extension] = MediaFileType(fileType, mimeType)
        sMimeTypeMap[mimeType] = fileType
    }
    init
    {
        addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg")
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4")
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav")
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr")
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb")
        addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma")
        addFileType("OGG", FILE_TYPE_OGG, "application/ogg")

        addFileType("MID", FILE_TYPE_MID, "audio/midi")
        addFileType("XMF", FILE_TYPE_MID, "audio/midi")
        addFileType("RTTTL", FILE_TYPE_MID, "audio/midi")
        addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi")
        addFileType("IMY", FILE_TYPE_IMY, "audio/imelody")

        addFileType("MP4", FILE_TYPE_MP4, "video/mp4")
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4")
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp")
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp")
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2")
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2")
        addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv")

        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg")
        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg")
        addFileType("GIF", FILE_TYPE_GIF, "image/gif")
        addFileType("PNG", FILE_TYPE_PNG, "image/png")
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp")
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp")

        addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl")
        addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls")
        addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl")

        // compute file extensions list for native Media Scanner
        val builder =  StringBuilder()
        val iterator = sFileTypeMap.keys.iterator()

        while (iterator.hasNext()) {
            if (builder.isNotEmpty()) {
                builder.append(',')
            }
            builder.append(iterator.next())
        }
        sFileExtensions = builder.toString()
    }

    const val UNKNOWN_STRING = "<unknown>"

    fun isAudioFileType(fileType: Int): Boolean {
        return fileType in FIRST_AUDIO_FILE_TYPE..LAST_AUDIO_FILE_TYPE ||
                fileType in FIRST_MIDI_FILE_TYPE..LAST_MIDI_FILE_TYPE
    }

    fun isVideoFileType(fileType: Int): Boolean {
        return fileType in FIRST_VIDEO_FILE_TYPE..LAST_VIDEO_FILE_TYPE
    }

    fun isImageFileType(fileType: Int): Boolean {
        return fileType in FIRST_IMAGE_FILE_TYPE..LAST_IMAGE_FILE_TYPE
    }

    fun isPlayListFileType(fileType: Int): Boolean {
        return fileType in FIRST_PLAYLIST_FILE_TYPE..LAST_PLAYLIST_FILE_TYPE
    }

    fun getFileType(path: String): MediaFileType? {
        val lastDot = path.lastIndexOf(".")
        val mediaType = if(lastDot< 0){path} else{path.substring(lastDot + 1).toUpperCase(Locale.ROOT)}
        return sFileTypeMap[mediaType]
    }

    //根据视频文件路径判断文件类型
    fun isVideoFileType(path: String): Boolean {
        val type = getFileType(path)
        return if (null != type) {
            isVideoFileType(type.fileType)
        } else false
    }

    //根据音频文件路径判断文件类型
    fun isAudioFileType(path: String): Boolean {
        val type = getFileType(path)
        return if (null != type) {
            isAudioFileType(type.fileType)
        } else false
    }

    //根据mime类型查看文件类型
    fun getFileTypeForMimeType(mimeType: String): Int {
        val value = sMimeTypeMap[mimeType]
        return value ?: 0
    }

    //根据图片文件路径判断文件类型
    fun isImageFileType(path: String): Boolean {
        val type = getFileType(path)
        return if (null != type) {
            isImageFileType(type.fileType)
        } else false
    }
}