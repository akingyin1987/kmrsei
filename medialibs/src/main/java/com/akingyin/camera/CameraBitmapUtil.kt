/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.graphics.*
import android.text.TextPaint
import com.akingyin.base.utils.DateUtil
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
    const val MaxpOffset = 40

    // 保存图片的质量
    const val quality = 90

    const val  MAX_BITMAP = 2 * 1024 *1024

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
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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

        var img_src: Bitmap? = null
        var fos: FileOutputStream? = null
        try {
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            fos = FileOutputStream(File(path, img))
            img_src = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (rotat != 0) {
                val m = Matrix()
                m.setRotate(rotat.toFloat())
                img_src = Bitmap.createBitmap(img_src, 0, 0, img_src.width, img_src.height, m, true)
            }
            img_src.compress(Bitmap.CompressFormat.JPEG, quality, fos)
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
        return img_src
    }


    /**
     * 压缩图片至 960*540
     *
     * @param mBitmap
     * @param rotat
     * @param
     * @param path
     * @param img
     * @return
     */
    @JvmOverloads
    @Throws(Exception::class, Error::class)
    fun zipImageTo960x540(mBitmap: Bitmap, rotat: Int, time: Long = System.currentTimeMillis(), landscape: Boolean = false, fileDir: String, fileName: String):Boolean {
        var srcBitmap: Bitmap = mBitmap
        var fos: FileOutputStream? = null

        try {
            // 防止拍出的图片分辨率小于　960　*　540
            if (mBitmap.width < 540 || mBitmap.height < 960) {
                val magnifywidth = 540.0.toFloat() / mBitmap.width
                val magnifyheight = 960.toFloat() / mBitmap.height
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
            if (srcBitmap.width - NormHigth > MaxpOffset || mBitmap.height - NormHigth > MaxpOffset) {

                val sx = if (mBitmap.width > mBitmap.height) {
                    (960.0 / mBitmap.width).toFloat()
                } else {
                    (960.0 / mBitmap.height).toFloat()
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
                    m.preRotate(if (rotat - 90 < 0) 270F else (rotat - 90).toFloat(), srcBitmap.width.toFloat() / 2, srcBitmap.height.toFloat() / 2)
                }
                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, m, true)
            }
            val canvas = Canvas(srcBitmap)
            val p = Paint()
            p.color = Color.WHITE
            p.style = Paint.Style.FILL
            p.isAntiAlias = true
            p.isDither = true

            val paint = TextPaint()
            paint.textSize = 20f
            paint.isDither = true
            paint.color = Color.RED
            paint.isAntiAlias = true
            paint.isFakeBoldText = true
            paint.setShadowLayer(1.6f, 1.5f, 1.3f, Color.BLACK)

            val arrowTxt: String = if (time == 0L) {
                DateUtil.getNowTimeString()
            } else {
                DateUtil.millis2String(time)
            }
            val rect = Rect()
            paint.getTextBounds(arrowTxt, 0, arrowTxt.length, rect)
            val w = rect.width()
            val h = rect.height()
            val desPath = Path()
            val txtX = mBitmap.width - 210.toFloat()
            val txtY = mBitmap.height - 10.toFloat()
            desPath.moveTo(txtX - 5, txtY - h - 5)
            desPath.lineTo(txtX - 5, txtY + 8)
            desPath.lineTo(txtX + w + 5, txtY + 8)
            desPath.lineTo(txtX + w + 5, txtY - h - 5)
            canvas.drawPath(desPath, p)
            canvas.drawText(arrowTxt, srcBitmap.width - 210.toFloat(), srcBitmap.height - 10.toFloat(), paint)
            canvas.save()
            println("data-保存照片->>>$arrowTxt")
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


}