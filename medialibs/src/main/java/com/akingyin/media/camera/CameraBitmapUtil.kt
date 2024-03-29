/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camera

import android.annotation.SuppressLint
import android.graphics.*

import android.text.TextPaint
import android.text.TextUtils
import android.util.DisplayMetrics
import androidx.exifinterface.media.ExifInterface
import com.akingyin.base.ext.appServerTime
import com.akingyin.base.utils.DateUtil
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/17 16:30
 * @version V1.0
 */
object CameraBitmapUtil {

     const val NormWidth = 540
     const val NormHigth = 960

    // 最大偏移量
    private const val MaxpOffset = 20

    // 保存图片的质量
    private const val quality = 90

    private const val  MAX_BITMAP = 300 *1024

    fun   dataToJpgFile(data: ByteArray, dir: String, fileName: String, rotat: Int):Bitmap?{
         val  file = File(dir,fileName)
          file.parentFile?.mkdirs()

        if(data.size > MAX_BITMAP){
            file.sink().use {
                it.buffer().use {
                    bufferedSink ->
                    bufferedSink.write(data)
                }
            }

           val options =  BitmapFactory.Options()
            // 先将inJustDecodeBounds设置为true不会申请内存去创建Bitmap，返回的是一个空的Bitmap，但是可以获取
            //图片的一些属性，例如图片宽高，图片类型等等
            options.inJustDecodeBounds = true
            // 计算inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 960, 960)

            // 加载压缩版图片
            options.inJustDecodeBounds = false
            // 根据具体情况选择具体的解码方法
            var srcBitmap = BitmapFactory.decodeFile(file.absolutePath, options)
            if (rotat != 0) {
                val m = Matrix()
                m.setRotate(rotat.toFloat())
                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, m, true)
            }
            FileOutputStream(file).use {
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }
            return  srcBitmap
        }else{
            return dataToBaseBitmap(data,dir,fileName,rotat)
        }
    }


   private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // 原图片的宽高
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // 计算inSampleSize值
            while (halfHeight / inSampleSize >= reqHeight
                    && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    /**
     *
     * @param data
     * 数据
     * @param path
     * 路径
     * @param img
     * 图片名
     * @param rotat
     * 旋转角度
     * @return
     */

    @Throws(Exception::class, Error::class)
    fun dataToBaseBitmap(data: ByteArray, path: String, img: String, rotat: Int): Bitmap? {
        var imgSrc: Bitmap?
        var fos: FileOutputStream? = null
        try {
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }

            fos = FileOutputStream(File(path, img))
            imgSrc = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (rotat != 0) {
                val m = Matrix()
                m.setRotate(rotat.toFloat())
                imgSrc = Bitmap.createBitmap(imgSrc, 0, 0, imgSrc.width, imgSrc.height, m, true)
            }
            imgSrc.compress(Bitmap.CompressFormat.JPEG, quality, fos)
        } catch (e: Exception) {
            e.printStackTrace()

            throw e
        } catch (e: Error) {

            e.printStackTrace()
            throw  e
        } finally {
            if (null != fos) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return imgSrc
    }

    fun decodeSampledBitmapFromFile(imageFile: File, reqWidth: Int, reqHeight: Int): Bitmap {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageFile.absolutePath, this)

            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            inJustDecodeBounds = false
            BitmapFactory.decodeFile(imageFile.absolutePath, this)
        }
    }


    /**
     * 压缩图片至 960*540
     *
     * @param mBitmap
     * @param rotat
     * @param

     * @return
     */
    @JvmOverloads
    @Throws(Exception::class, Error::class)
    fun zipImageTo960x540(mBitmap: Bitmap, rotat: Int, time: Long = System.currentTimeMillis(), landscape: Boolean = false, fileDir: String, fileName: String):Boolean {
        var srcBitmap: Bitmap = mBitmap
        var fos: FileOutputStream? = null

        try {
            // 防止拍出的图片分辨率小于　960　*　540
            if (mBitmap.width < NormWidth || mBitmap.height < NormHigth) {
                val magnifywidth = NormWidth.toFloat() / mBitmap.width
                val magnifyheight = NormHigth.toFloat() / mBitmap.height
                val magnify = magnifywidth.coerceAtLeast(magnifyheight)
                val matrix = Matrix()
                matrix.postScale(magnify, magnify)
                // 长和宽放大缩小的比例
                srcBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.width, mBitmap.height, matrix, true)
            }
            var file = File(fileDir)
            if (!file.exists()) {
                file.mkdirs()
            }
            val m = Matrix()
            if (srcBitmap.width - NormHigth > MaxpOffset || srcBitmap.height - NormHigth > MaxpOffset) {

                val sx = if (mBitmap.width > mBitmap.height) {
                    (NormHigth.toFloat() / mBitmap.width)
                } else {
                    (NormHigth.toFloat() / mBitmap.height)
                }

                m.postScale(sx, sx)
            }
            if (landscape) {
                if (srcBitmap.width < srcBitmap.height) {
                    var degree = 90
                    if (rotat == 0) {
                        degree = 270
                    }
                    m.preRotate(degree.toFloat(), srcBitmap.width.toFloat() / 2, srcBitmap.height.toFloat() / 2)
                    srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, m, true)
                } else {
                    srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, m, true)
                }
            } else {

                if (rotat != 90) {
                    m.preRotate(if (rotat - 90 < 0) 270f else (rotat - 90).toFloat(), srcBitmap.width.toFloat() / 2, srcBitmap.height.toFloat() / 2)
                }
                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, m, true)
            }
            val canvas = Canvas(srcBitmap)
            val p = Paint().apply {
                color = Color.WHITE
                style = Paint.Style.FILL
                isAntiAlias = true
            }

            val paint = TextPaint().apply {
               textSize = 20f
                color = Color.RED
               isAntiAlias = true
            }


            val arrowTxt: String = if (time == 0L) {
                DateUtil.millis2String(appServerTime)
            } else {
                DateUtil.millis2String(time)
            }
            val rect = Rect()
            paint.getTextBounds(arrowTxt, 0, arrowTxt.length, rect)
            val w = rect.width()
            val h = rect.height()
            val desPath = Path()
            val txtX = srcBitmap.width - 210.toFloat()
            val txtY = srcBitmap.height - 10.toFloat()
            desPath.moveTo(txtX - 5, txtY - h - 5)
            desPath.lineTo(txtX - 5, txtY + 8)
            desPath.lineTo(txtX + w + 5, txtY + 8)
            desPath.lineTo(txtX + w + 5, txtY - h - 5)
            canvas.drawPath(desPath, p)
            canvas.drawText(arrowTxt, srcBitmap.width - 210.toFloat(), srcBitmap.height - 10.toFloat(), paint)
            canvas.save()

            file = File(fileDir, fileName)
            fos = FileOutputStream(file)
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)


            return true
        } catch (e: Exception) {
            e.printStackTrace()
            throw  e
        } catch (e: Error) {
            e.printStackTrace()
            throw  e
        } finally {
            if (null != fos) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mBitmap.recycle()
            if(srcBitmap.isRecycled){
                srcBitmap.recycle()
            }
            System.gc()
        }


    }

    /**
     * 将bitmap 转成图片文件
     */
    fun saveBitmapToPath(bitmap: Bitmap, filePath: String): Boolean {

        var result = false //默认结果
        val file = File(filePath)
        try {
             result = FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

            bitmap.recycle()
        }
        return result
    }

    /**
     * 缩放Bitmap
     *
     * @param bitmap
     * @param scale
     * @return
     */
    fun bitmapScale(bitmap: Bitmap, scale: Float): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    /**
     * 旋转图片文件
     */
    @SuppressLint("RestrictedApi")
    fun rotateBitmap(degree: Int, originalPath:String, localPath: String, time: Long) :Boolean{
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeFile(originalPath)
            val oldExifInterface = ExifInterface(originalPath)

            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            val savebmp = saveBitmapToPath(resizeBitmap, localPath)
            if (savebmp) {
                imgSdf.get()?.let {
                    val exifInterface = ExifInterface(localPath)
                    exifInterface.setDateTime(appServerTime)
                    oldExifInterface.latLong?.let {
                        exifInterface.setLatLong(it[0],it[1])
                    }
                    oldExifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)?.let {
                        exifInterface.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD,it)
                    }
                    oldExifInterface.getAttribute(ExifInterface.TAG_USER_COMMENT)?.let {
                        exifInterface.setAttribute(ExifInterface.TAG_USER_COMMENT,it)
                    }
                    exifInterface.saveAttributes()


                }

            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: Error) {
            e.printStackTrace()
        } finally {
            bitmap?.recycle()
        }
        return false
    }
    const val TAG_DATETIME = "YYYY:MM:DD HH:MM:SS"
    private val imgSdf: ThreadLocal<SimpleDateFormat> = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat(TAG_DATETIME, Locale.CHINA)
        }
    }
    fun getExifinterAttr(localPath: String, key: String): String {
        if (TextUtils.isEmpty(localPath)) {
            return ""
        }
        try {
            val exifInterface = ExifInterface(localPath)
            return exifInterface.getAttribute(key)?:""
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }


    fun saveExifinterAttr(localpath: String, key: String, value: String) {
        try {

            val exifInterface = ExifInterface(localpath)
            exifInterface.setAttribute(key, value)
            exifInterface.saveAttributes()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun saveExifinterAttr(localpath: String, data: Map<String, String>) {
        try {

            val exifInterface = ExifInterface(localpath)
            for (key in data.keys) {
                exifInterface.setAttribute(key, data[key])
            }

            exifInterface.saveAttributes()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取缩小的比例(主要用于涂鸦显示)
     *
     * @param bitmap
     * @param dm
     * @return
     */
    var minScale = 7 / 8f
    fun getBitmapScale(bitmap: Bitmap, dm: DisplayMetrics): Float {

        var width = bitmap.width
        var heigth = bitmap.height
        if (width > heigth) {
            width = heigth
            heigth = bitmap.width
        }
        val screenWidth = dm.widthPixels.coerceAtMost(dm.heightPixels)
        val screenHight = dm.widthPixels.coerceAtLeast(dm.heightPixels)

        // 当前图片比屏幕大
        if (width > screenWidth || heigth > screenHight) {
            val x: Float = (screenWidth - MaxpOffset) / width.toFloat()
            val y: Float = (screenHight - MaxpOffset) / heigth.toFloat()
            return if (x <= y) {
                x
            } else y
        }

        // 　图片至少应占屏幕的4/5
        if (width * (1 / minScale) < screenWidth || heigth * (1 / minScale) < screenHight) {
            val x = screenWidth.toFloat() * minScale / width
            val y = screenHight.toFloat() * minScale / heigth

            return x.coerceAtMost(y)
        }
        return 0f
    }

}