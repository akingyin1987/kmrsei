/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.bmap

import com.akingyin.base.utils.ConvertUtils
import com.akingyin.map.TestUtil
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.URLEncoder
import java.util.*

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2021/4/15 10:14
 */
class BDMapManagerTest{

    @Test
    fun  testBaiduSn(){

        val sing="f32104a4115912102d080caf2c69ec53b0bbf852"

        val str = sing.chunked(2).joinToString(":").toUpperCase()+";com.zlcdgroup.mrsei"
        val latlng = TestUtil.Latlng()
       val uri = BDMapManager.getBdMapStaticImageUrl(29.523678999999998,106.52564699999999,"bd09ll",str)
        println(uri)
    }


    @Test
    fun  testSign(){
        val sing="f32104a4115912102d080caf2c69ec53b0bbf852"

       val str = sing.chunked(2).joinToString(":").toUpperCase()
        println(str)

    }

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }
}