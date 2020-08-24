/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

@file:Suppress("DEPRECATION")

package com.akingyin.media

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.text.TextUtils
import androidx.exifinterface.media.ExifInterface
import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.utils.IOUtils
import java.io.InputStream
import java.text.SimpleDateFormat
import kotlin.math.abs

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/14 11:50
 * @version V1.0
 */
object MediaUtils {

    private val sf by lazy { SimpleDateFormat("yyyyMMdd_HHmmssSS") }

    fun  getCreateFileName(prefix:String):String{
        val millis = System.currentTimeMillis()
        return prefix + sf.format(millis)
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @param suffixType
     * @return 图片的uri
     */
    fun createImageUri(context: Context, suffixType: String?): Uri? {
        val imageFilePath = arrayOf<Uri?>(null)
        val status = Environment.getExternalStorageState()
        val time: String = System.currentTimeMillis().toString()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, getCreateFileName("IMG_"))
        values.put(MediaStore.Images.Media.DATE_TAKEN, time)
        values.put(MediaStore.Images.Media.MIME_TYPE, if (TextUtils.isEmpty(suffixType)) MediaMimeType.MIME_TYPE_IMAGE else suffixType)
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status == Environment.MEDIA_MOUNTED) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, MediaMimeType.DCIM)
            imageFilePath[0] = context.contentResolver
                    .insert(MediaStore.Images.Media.getContentUri("external"), values)
        } else {
            imageFilePath[0] = context.contentResolver
                    .insert(MediaStore.Images.Media.getContentUri("internal"), values)
        }
        return imageFilePath[0]
    }

    /**
     * 创建一条视频地址uri,用于保存录制的视频
     *
     * @param context
     * @param suffixType
     * @return 视频的uri
     */
    fun createVideoUri(context: Context, suffixType: String?): Uri? {
        val imageFilePath = arrayOf<Uri?>(null)
        val status = Environment.getExternalStorageState()
        val time: String = System.currentTimeMillis().toString()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        values.put(MediaStore.Video.Media.DISPLAY_NAME, getCreateFileName("VID_"))
        values.put(MediaStore.Video.Media.DATE_TAKEN, time)
        values.put(MediaStore.Video.Media.MIME_TYPE, if (TextUtils.isEmpty(suffixType)) MediaMimeType.MIME_TYPE_VIDEO else suffixType)
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status == Environment.MEDIA_MOUNTED) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            imageFilePath[0] = context.contentResolver
                    .insert(MediaStore.Video.Media.getContentUri("external"), values)
        } else {
            imageFilePath[0] = context.contentResolver
                    .insert(MediaStore.Video.Media.getContentUri("internal"), values)
        }
        return imageFilePath[0]
    }

    /**
     * 获取视频时长
     *
     * @param context
     * @param isAndroidQ
     * @param path
     * @return
     */
    fun extractDuration(context: Context,  path: String): Long {
        val  sdk = Build.VERSION.SDK_INT
        return if (sdk >= Build.VERSION_CODES.Q) getLocalDuration(context, Uri.parse(path)) else getLocalDuration(path)
    }


    private fun getLocalDuration(path: String): Long {
        return try {
            MediaMetadataRetriever().use {
                it.setDataSource(path)
                return@use it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?:0
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * get Local video duration
     *
     * @return
     */
    private fun getLocalDuration(context: Context, uri: Uri): Long {
        return try {
             MediaMetadataRetriever().use {
                 it.setDataSource(context,uri)
                 return@use it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?:0
             }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 是否是长图
     *
     * @param width  宽
     * @param height 高
     * @return true 是 or false 不是
     */
    fun isLongImg(width: Int, height: Int): Boolean {
        val newHeight = width * 3
        return height > newHeight
    }

    /**
     * get Local image width or height for api 29
     *
     * @return
     */
    fun getImageSizeForUrlToAndroidQ(context: Context, url: String?): IntArray? {
        val size = IntArray(2)
        var query: Cursor? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                query = context.applicationContext.contentResolver
                        .query(Uri.parse(url),
                                null, null, null)
                if (query != null) {
                    query.moveToFirst()
                    size[0] = query.getInt(query.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
                    size[1] = query.getInt(query.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            query?.close()
        }
        return size
    }

    /**
     * get Local video width or height
     *
     * @return
     */
    fun getVideoSizeForUrl(url: String?): IntArray? {
        val size = IntArray(2)
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(url)
            size[0] =(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))?.toInt()?:0
            size[1] =(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))?.toInt()?:0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * get Local video width or height
     *
     * @return
     */
    fun getVideoSizeForUri(context: Context?, uri: Uri?): IntArray? {
        val size = IntArray(2)
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, uri)
            size[0] = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))?.toInt()?:0
            size[1] = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))?.toInt()?:0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * get Local image width or height
     *
     * @return
     */
    fun getImageSizeForUrl(url: String): IntArray {
        val size = IntArray(2)
        try {
            val exifInterface = ExifInterface(url)
            // 获取图片的宽度
            val width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL)
            // 获取图片的高度
            val height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL)
            size[0] = width
            size[1] = height
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return size
    }


    /**
     * get Local image width or height
     *
     * @return
     */
    fun getImageSizeForUri(context: Context, uri: Uri?): IntArray? {
        val size = IntArray(2)
        var fileDescriptor: ParcelFileDescriptor? = null
        try {
            fileDescriptor = context.contentResolver.openFileDescriptor(uri!!, "r")
            if (fileDescriptor != null) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFileDescriptor(fileDescriptor.fileDescriptor, null, options)
                size[0] = options.outWidth
                size[1] = options.outHeight
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
          fileDescriptor?.close()
        }
        return size
    }


    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     */
    fun removeMedia(context: Context, id: Int) {
        try {
            val cr = context.applicationContext.contentResolver
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Images.Media._ID + "=?"
            cr.delete(uri, selection, arrayOf(java.lang.Long.toString(id.toLong())))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    fun getDCIMLastImageId(context: Context): Int {
        var data: Cursor? = null
        return try {
            //selection: 指定查询条件
            val absolutePath: String = AppFileConfig.APP_FILE_ROOT
            val orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
            val selection = MediaStore.Images.Media.DATA + " like ?"
            //定义selectionArgs：
            val selectionArgs = arrayOf("$absolutePath%")
            data = context.applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    selection, selectionArgs, orderBy)
            if (data != null && data.count > 0 && data.moveToFirst()) {
                val id = data.getInt(data.getColumnIndex(MediaStore.Images.Media._ID))
                val date = data.getLong(data.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                val duration: Int = abs(System.currentTimeMillis()- date).toInt()
                // DCIM文件下最近时间1s以内的图片，可以判定是最新生成的重复照片
                if (duration <= 1) id else -1
            } else {
                -1
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            -1
        } finally {
            data?.close()
        }
    }

    /**
     * 获取Camera文件下最新一条拍照记录
     *
     * @return
     */
    fun getCameraFirstBucketId(context: Context): Long {
        var data: Cursor? = null
        try {
            val absolutePath: String = AppFileConfig.APP_FILE_ROOT
            //selection: 指定查询条件
            val selection = MediaStore.Files.FileColumns.DATA + " like ?"
            //定义selectionArgs：
            val selectionArgs = arrayOf("$absolutePath%")
            val orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
            data = context.applicationContext.contentResolver.query(MediaStore.Files.getContentUri("external"), null,
                    selection, selectionArgs, orderBy)
            if (data != null && data.count > 0 && data.moveToFirst()) {
                return data.getLong(data.getColumnIndex("bucket_id"))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            data?.close()
        }
        return -1
    }


    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    fun getAudioFilePathFromUri(context: Context, uri: Uri?): String? {
        var path = ""
        var cursor: Cursor? = null
        try {
            cursor = context.applicationContext.contentResolver
                    .query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
                path = cursor.getString(index)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return path
    }


    /**
     * 获取旋转角度
     *
     * @param path
     * @return
     */
    fun getVideoOrientationForUrl(path: String?): Int {
        return try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(path)
            val rotation: Int = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))?.toInt()?:0
            when (rotation) {
                90 -> ExifInterface.ORIENTATION_ROTATE_90
                270 -> ExifInterface.ORIENTATION_ROTATE_270
                else -> 0
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 获取旋转角度
     *
     * @param uri
     * @return
     */
    fun getVideoOrientationForUri(context: Context, uri: Uri?): Int {
        return try {
            MediaMetadataRetriever().use {
                it.setDataSource(context, uri)

              return@use  when (it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt()?:0) {
                    90 -> ExifInterface.ORIENTATION_ROTATE_90
                    270 -> ExifInterface.ORIENTATION_ROTATE_270
                    else -> 0

                }
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
        }finally {

        }
    }

    /**
     * 获取旋转角度
     *
     * @param context
     * @param url
     * @return
     */
    fun getImageOrientationForUrl(context: Context, url: String): Int {
        var exifInterface: ExifInterface? = null
        var inputStream: InputStream? = null
        return try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q && MediaMimeType.isContent(url)) {
                inputStream = context.contentResolver.openInputStream(Uri.parse(url))
                if (inputStream != null) {
                    exifInterface = ExifInterface(inputStream)
                }
            } else {
                exifInterface = ExifInterface(url)
            }
            exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                ?: 0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
        } finally {
            IOUtils.close(inputStream)
        }
    }


}