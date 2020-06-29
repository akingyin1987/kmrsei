/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.media.model

import java.io.Serializable

/**
 * 图文集合
 * @author king
 * @version V1.0
 * @ Description:
 *
 *
 * @ Date 2017/12/5 11:35
 */
class ImageTextList : Serializable {
    var items: List<ImageTextModel>? = null

    constructor() {}
    constructor(items: List<ImageTextModel>?) {
        this.items = items
    }
}