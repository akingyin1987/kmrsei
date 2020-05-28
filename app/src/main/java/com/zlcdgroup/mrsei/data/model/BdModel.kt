/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.data.model

import com.akingyin.map.IMarker
import com.akingyin.map.TestUtil

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/28 11:32
 * @version V1.0
 */
class BdModel(uuid: String ) : IMarker(uuid) {
    override fun getLat() = TestUtil.Latlng()[0]
    override fun getLng() = TestUtil.Latlng()[1]

    override fun isComplete()= false


}