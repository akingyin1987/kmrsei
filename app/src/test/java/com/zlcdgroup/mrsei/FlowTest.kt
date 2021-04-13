/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.junit.Test

/**
 * @ Description:
 * @author king
 * @ Date 2021/3/13 15:04
 * @version V1.0
 */
class FlowTest {
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    @Test
    fun  test1(){
        val ints  = sequence {
            (1..5).forEach {
                yield(it)
                println(it)

            }
        }
        println(ints)
        for (value in ints) {
            println("value=$value")
        }
    }

    @InternalCoroutinesApi
    @Test
    fun  test2(){
        val intFlow  = flow {
            (1..5).forEach {
                emit(it)
                println(it)
                delay(100)
            }
        }

        GlobalScope.launch(Dispatchers.IO) {

        }
    }
}