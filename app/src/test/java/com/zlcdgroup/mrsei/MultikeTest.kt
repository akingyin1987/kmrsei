/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.forEach
import org.junit.Test

/**
 * @ Description:
 * @author king
 * @ Date 2021/3/9 16:16
 * @version V1.0
 */
class MultikeTest {

    @Test
    fun  test1(){
        val a = mk.ndarray(mk[1,2,3])
        println("value=>${a}")
        a.forEach {
            println("it=$it")
        }
    }

    @Test
    fun  test2(){
        //通过集合创建向量
        val mylist = listOf(1,2,3)
        val a = mk.ndarray(mylist)
    }

    @Test
    fun  test3(){
        val myList = listOf(1,2,3)
        val m = mk.ndarray(mk[myList,myList])
    }
}