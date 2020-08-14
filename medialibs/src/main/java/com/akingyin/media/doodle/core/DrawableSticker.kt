/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.IntRange

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/13 14:03
 * @version V1.0
 */
open class DrawableSticker( d: Drawable) : Sticker(){


    private var realBounds: Rect
    private var drawable: Drawable

    init {
        drawable = d
        realBounds = Rect(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
    }




    override fun draw( canvas: Canvas) {
        canvas.save()
        canvas.concat(matrix)
        drawable.bounds = realBounds

        drawable.draw(canvas)
        canvas.restore()
    }


    override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int): DrawableSticker {
        drawable.alpha = alpha
        return this
    }

    override fun getWidth(): Int {
        return drawable.intrinsicWidth
    }

    override fun getHeight(): Int {
        return drawable.intrinsicHeight
    }

    override fun setDrawable(drawable: Drawable): Sticker {
        this.drawable = drawable
        return  this
    }

    override fun getDrawable()= drawable


}