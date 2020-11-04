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
import android.text.TextUtils


/**
 * 含某类型的图文集合
 *
 */
class MediaDataListTypeModel() :  Parcelable {

    var text: String=""
    var items: List<MediaDataModel>? = null

    constructor(parcel: Parcel) : this() {
        text = parcel.readString()?:""
        items = parcel.createTypedArrayList(MediaDataModel.CREATOR)
    }


    override fun toString(): String {
        return if (!TextUtils.isEmpty(text)) {
            text
        } else super.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaDataListTypeModel> {
        override fun createFromParcel(parcel: Parcel): MediaDataListTypeModel {
            return MediaDataListTypeModel(parcel)
        }

        override fun newArray(size: Int): Array<MediaDataListTypeModel?> {
            return arrayOfNulls(size)
        }
    }
}