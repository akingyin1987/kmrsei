/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/3 12:30
 * @version V1.0
 */
 class GalleryDataVo() : Parcelable {
    var data: List<String>? = null

    constructor(parcel: Parcel) : this() {
        data = parcel.createStringArrayList()
    }



    companion object CREATOR : Parcelable.Creator<GalleryDataVo> {
        override fun createFromParcel(parcel: Parcel): GalleryDataVo {
            return GalleryDataVo(parcel)
        }

        override fun newArray(size: Int): Array<GalleryDataVo?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(data)
    }

    override fun describeContents(): Int {
        return 0
    }
}