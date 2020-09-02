/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 15:45
 * @version V1.0
 */
class BleDevice(var mDevice: BluetoothDevice?) : Parcelable {
    //var mDevice: BluetoothDevice? = null
    var mScanRecord: ByteArray? = null
    var mRssi = 0
    var mTimestampNanos: Long = 0

    constructor(parcel: Parcel) : this(parcel.readParcelable<BluetoothDevice>(BluetoothDevice::class.java.classLoader)) {
        mScanRecord = parcel.createByteArray()
        mRssi = parcel.readInt()
        mTimestampNanos = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(mDevice, flags)
        parcel.writeByteArray(mScanRecord)
        parcel.writeInt(mRssi)
        parcel.writeLong(mTimestampNanos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BleDevice> {
        override fun createFromParcel(parcel: Parcel): BleDevice {
            return BleDevice(parcel)
        }

        override fun newArray(size: Int): Array<BleDevice?> {
            return arrayOfNulls(size)
        }
    }

    fun getName(): String {
       return  mDevice?.name?:""
    }

    fun getMac(): String {
        return mDevice?.address?:""
    }

    fun getKey(): String {
        return getName()+getMac()
    }


}