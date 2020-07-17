/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera

import android.os.Parcel
import android.os.Parcelable
import android.util.Size
import androidx.annotation.NonNull

import androidx.collection.SparseArrayCompat
import com.akingyin.base.utils.CalculationUtil


/**
 * 屏宽 比
 * @ Description:
 * @author king
 * @ Date 2020/7/16 16:09
 * @version V1.0
 */
class AspectRatio(var mX:Int,var mY:Int) : Comparator<AspectRatio>,Parcelable {


    companion object{
        private val sCache = SparseArrayCompat<SparseArrayCompat<AspectRatio>>(16)
        val CREATOR: Parcelable.Creator<AspectRatio> = object : Parcelable.Creator<AspectRatio> {
            override fun createFromParcel(source: Parcel): AspectRatio {
                val x = source.readInt()
                val y = source.readInt()
                return of(x, y)
            }

            override fun newArray(size: Int): Array<AspectRatio?> {
                return arrayOfNulls(size)
            }
        }
        fun of(x: Int, y: Int): AspectRatio {

            var x = x
            var y = y
            val gcd: Int = CalculationUtil.MaxCommonDivisor2(x, y)
            x /= gcd
            y /= gcd
            var arrayX: SparseArrayCompat<AspectRatio>? = sCache[x]
            return if (arrayX == null) {
                val ratio = AspectRatio(x, y)
                arrayX = SparseArrayCompat()
                arrayX.put(y, ratio)
                sCache.put(x, arrayX)
                ratio
            } else {
                var ratio = arrayX[y]
                if (ratio == null) {
                    ratio = AspectRatio(x, y)
                    arrayX.put(y, ratio)
                }
                ratio
            }
        }
    }



    /**
     * Parse an [AspectRatio] from a [String] formatted like "4:3".
     *
     * @param s The string representation of the aspect ratio
     * @return The aspect ratio
     * @throws IllegalArgumentException when the format is incorrect.
     */
    fun parse(s: String): AspectRatio? {
        val position = s.indexOf(':')
        require(position != -1) { "Malformed aspect ratio: $s" }
        return try {
            val x = s.substring(0, position).toInt()
            val y = s.substring(position + 1).toInt()
            of(x, y)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Malformed aspect ratio: $s", e)
        }
    }

    private fun AspectRatio(x: Int, y: Int) {
        mX = x
        mY = y
    }

    fun getX(): Int {
        return mX
    }

    fun getY(): Int {
        return mY
    }

    fun matches(size: Size): Boolean {
        val gcd = CalculationUtil.MaxCommonDivisor2(size.width, size.height)
        val x: Int = size.width / gcd
        val y: Int = size.height / gcd
        return mX == x && mY == y
    }

    override fun equals(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        if (this === o) {
            return true
        }
        if (o is AspectRatio) {
            return mX == o.mX && mY == o.mY
        }
        return false
    }

    override fun toString(): String {
        return "$mX:$mY"
    }

    private fun toFloat(): Float {
        return mX.toFloat() / mY
    }

    override fun hashCode(): Int {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return mY xor (mX shl Integer.SIZE / 2 or (mX ushr Integer.SIZE / 2))
    }

    override fun compare(o1: AspectRatio, o2: AspectRatio): Int {
        if(o1 == o2){
            return 0
        } else if(o1.toFloat() - o2.toFloat()>0){
            return 1
        }
        return -1
    }

    operator fun compareTo(@NonNull another: AspectRatio): Int {
        if (equals(another)) {
            return 0
        } else if (toFloat() - another.toFloat() > 0) {
            return 1
        }
        return -1
    }

    /**
     * @return The inverse of this [AspectRatio].
     */
    fun inverse(): AspectRatio? {
        return of(mY, mX)
    }









    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mX)
        parcel.writeInt(mY)
    }

    override fun describeContents(): Int {
        return 0
    }


}