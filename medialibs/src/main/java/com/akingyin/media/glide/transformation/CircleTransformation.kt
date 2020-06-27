/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.glide.transformation

import android.graphics.*

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


/**
 *
 * @ Description:
 * @author king
 * @ Date 2020/6/27 18:05
 * @version V1.0
 */
class CircleTransformation : BitmapTransformation() {
    private val ID = javaClass.name
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(CHARSET))
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val size = toTransform.width.coerceAtMost(toTransform.height)
        val x = (toTransform.width - size) / 2
        val y = (toTransform.height - size) / 2


        val square = Bitmap.createBitmap(toTransform, x, y, size, size)
        val circle = pool[size, size, Bitmap.Config.ARGB_8888]

        val canvas = Canvas(circle)
        val paint = Paint()
        paint.shader = BitmapShader(square, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        return circle
    }
}