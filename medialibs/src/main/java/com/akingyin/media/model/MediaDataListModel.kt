/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.media.model

import android.os.Parcel
import android.os.Parcelable


/**
 * 图文数据集合
 * @author king
 * @version V1.0
 * @ Description:
 */
class MediaDataListModel() :  Parcelable {
    var items: List<MediaDataModel>? = null

    constructor(parcel: Parcel) : this() {
        items = parcel.createTypedArrayList(MediaDataModel.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaDataListModel> {
        override fun createFromParcel(parcel: Parcel): MediaDataListModel {
            return MediaDataListModel(parcel)
        }

        override fun newArray(size: Int): Array<MediaDataListModel?> {
            return arrayOfNulls(size)
        }
    }


}