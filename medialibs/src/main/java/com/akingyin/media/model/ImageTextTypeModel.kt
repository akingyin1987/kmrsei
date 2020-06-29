/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.media.model

import android.text.TextUtils
import java.io.Serializable

/**
 * 含类型的图文集合
 * Created by Administrator on 2016/10/10.
 */
class ImageTextTypeModel : Serializable {

    var text: String=""
    var items: List<ImageTextModel>? = null

    constructor(text: String, items: List<ImageTextModel>?) {
        this.text = text
        this.items = items
    }

    constructor() {}

    override fun toString(): String {
        return if (!TextUtils.isEmpty(text)) {
            text
        } else super.toString()
    }
}