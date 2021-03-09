/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test

/**
 * @ Description:
 * @author king
 * @ Date 2021/3/9 16:39
 * @version V1.0
 */
class KotlinFlowTest {

    @Test
    fun  test1(){
        val flow : Flow<Int> = flow {
            delay(100)
            for (i in 1..10){
                emit(i)
            }
        }.map {
           // delay(100)
            it * it
        }


        GlobalScope.launch {
            flow.collect {
                delay(100)
                println("it=$it")
            }
            flow.collectLatest {
                delay(100)
                println("last=$it")


            }
            flow.catch {
                println("这是异常")
            }
        }
        Thread.sleep(10000)
    }



    @Test
    fun test2(){
        val ints = sequence {
            (1..3).forEach {
                yield(it)
                println("a=$it")
            }
        }
        ints.iterator().forEach {
            println("it==$it")
        }


    }


    @Test
    fun test3(){
        GlobalScope.launch {
            flow {
                List(100){
                    println("发送->$it")
                    emit(it)
                }
            }.collectLatest {
                delay(100)
                println("value->${it}")
            }
        }
        Thread.sleep(5000)

    }
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int>  get() = _state

    // 参数一：当新的订阅者Collect时，发送几个已经发送过的数据给它
    // 参数二：减去replay，MutableSharedFlow还缓存多少数据
    // 参数三：缓存策略，三种 丢掉最新值、丢掉最旧值和挂起
    val sharedState = MutableSharedFlow<Int>(5,3,BufferOverflow.DROP_OLDEST)

    @Before
    fun  init(){
       _state.value = 1
       sharedState.tryEmit(1)
    }

    @Test
    fun test4(){
       GlobalScope.launch {
           List(100){
               delay(100)
               _state.value = it
               sharedState.tryEmit(1)
           }

       }

       GlobalScope.launch {
           state.collect {
               println("it====>$it")
               println(state.replayCache)
               println(sharedState.replayCache)
           }

       }
       GlobalScope.launch {

       }

       Thread.sleep(15000)
    }


}