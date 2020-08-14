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
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.annotation.IntDef
import com.akingyin.media.doodle.event.StickerIconEvent

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/13 14:57
 * @version V1.0
 */
class BitmapStickerIcon(drawable: Drawable,@Gravity var   gravity:Int = Gravity.LEFT_TOP) :  DrawableSticker(drawable) , StickerIconEvent {

     var iconRadius: Float = DEFAULT_ICON_RADIUS

     var x = 0f
     var y = 0f

    @Gravity
     var position: Int =Gravity.LEFT_TOP

    init {
        position = gravity
    }

     var iconEvent: StickerIconEvent? = null

    @IntDef(value = [Gravity.LEFT_TOP, Gravity.RIGHT_TOP, Gravity.LEFT_BOTTOM, Gravity.RIGHT_BOTOM])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Gravity{
        companion object{
            const val LEFT_TOP = 0
            const val RIGHT_TOP = 1
            const val LEFT_BOTTOM = 2
            const val RIGHT_BOTOM = 3
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(x, y, iconRadius, paint)
        super.draw(canvas)
    }




    override fun onActionDown(stickerView: IDoodle, event: MotionEvent) {
        iconEvent?.onActionDown(stickerView,event)
    }

    override fun onActionMove(stickerView: IDoodle, event: MotionEvent) {
         iconEvent?.onActionMove(stickerView,event)
    }

    override fun onActionUp(stickerView: IDoodle, event: MotionEvent) {
       iconEvent?.onActionUp(stickerView,event)
    }
    companion object{
        const val DEFAULT_ICON_RADIUS = 30f
      //  const val DEFAULT_ICON_EXTRA_RADIUS = 10f

    }
}