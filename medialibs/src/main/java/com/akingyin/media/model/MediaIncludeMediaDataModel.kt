/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 多个不同类型分类图形集合
 * @ Description:
 * @author king
 * @ Date 2020/11/4 16:15
 * @version V1.0
 */
class MediaIncludeMediaDataModel() : Parcelable {

    var  items : List<MediaDataListTypeModel>?=null

    constructor(parcel: Parcel) : this() {
        items = parcel.createTypedArrayList(MediaDataListTypeModel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaIncludeMediaDataModel> {
        override fun createFromParcel(parcel: Parcel): MediaIncludeMediaDataModel {
            return MediaIncludeMediaDataModel(parcel)
        }

        override fun newArray(size: Int): Array<MediaIncludeMediaDataModel?> {
            return arrayOfNulls(size)
        }
    }
}