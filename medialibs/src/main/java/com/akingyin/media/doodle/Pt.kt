/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/10 15:33
 * @version V1.0
 */
data class Pt(var x:Int,var y:Int) {

    override fun toString(): String {
        return "Pt(x=$x, y=$y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pt

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }


    fun   postOffset(offsetX:Float,offsetY:Float){
        x = (x +offsetX).toInt()
        y = (y+offsetY).toInt()
    }

    fun   isEmpty():Boolean = x ==0 && y ==0

}