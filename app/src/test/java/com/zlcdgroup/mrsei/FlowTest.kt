/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei

import org.junit.Test

/**
 * @ Description:
 * @author king
 * @ Date 2021/3/13 15:04
 * @version V1.0
 */
class FlowTest {

    @Test
    fun  test1(){
        val ints  = sequence {
            (1..5).forEach {
                yield(it)
                println(it)

            }
        }
        println(ints)

    }

    @Test
    fun  test2(){
        val ints  = sequence {
            (1..5).forEach {
                yield(it)
                println(it)

            }
        }
    }
}