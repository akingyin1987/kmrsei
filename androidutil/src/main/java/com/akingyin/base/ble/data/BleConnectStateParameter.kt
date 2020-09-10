/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/7 11:41
 * @version V1.0
 */
class BleConnectStateParameter() : Parcelable {

     var status = 0
     var isActive = false

    constructor(parcel: Parcel) : this() {
        status = parcel.readInt()
        isActive = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(status)
        parcel.writeByte(if (isActive) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BleConnectStateParameter> {
        override fun createFromParcel(parcel: Parcel): BleConnectStateParameter {
            return BleConnectStateParameter(parcel)
        }

        override fun newArray(size: Int): Array<BleConnectStateParameter?> {
            return arrayOfNulls(size)
        }
    }
}