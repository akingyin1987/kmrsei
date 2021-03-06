/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.view.MotionEvent
import androidx.annotation.IntDef
import java.io.File


/**
 * @ Description:
 * @author king
 * @ Date 2020/8/10 16:07
 * @version V1.0
 */
interface IDoodle {









     fun flipCurrentSticker(@Flip direction:Int)

    @IntDef(flag = true, value = [Flip.FLIP_HORIZONTALLY, Flip.FLIP_VERTICALLY])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Flip{
        companion object{
            const val FLIP_HORIZONTALLY = 1
            //左移运算符shl(bits)  相当于 2^n * i , n 移位位数 ， i操作数
            const val FLIP_VERTICALLY = 1 shl 1
        }
    }

     fun  zoomAndRotateCurrentSticker(event:MotionEvent)

    /**
     * 移除当前操作的图形
     */
    fun  removeCurrentDoodeShape()

    fun  remove(iDoodleShape: IDoodleShape):Boolean

    fun  removeAllDoodeShape()

    fun  addShape(iDoodleShape: IDoodleShape,@Sticker.Position postion:Int=Sticker.Position.CENTER)

    fun  replace(iDoodleShape: IDoodleShape,needStayState:Boolean = true):Boolean


    fun  onCreateSticketShape()

    /**
     * 保存涂鸦图片
     */
    fun  saveDoodleBitmap(file: File,scale:Float = 0f,call:(result:Boolean,error:String?)->Unit)

}