package com.zlcdgroup.mrsei.presenter.impl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * @ Description:
 * @author king
 * @ Date 2019/7/30 16:37
 * @version V1.0
 */
class CoroutinesTest {


    @Test
    fun  main () = runBlocking {
        println("--- main start ---")
        //创建任务list，若默认CommonPool线程数很多，可加大任务数量模拟，p.s. List(50)
        val deferredList = List(10) {
            serviceAsync(it)

        }
        //并行启动任务，模拟大量请求下的并发情况

        deferredList.parallelStream().forEach {
            runBlocking {
                println("start")
                println("${it.await()} end")
            }
        }
        //死锁发生、永远不会执行到这里
        println("--- main end ---")
    }


    /**
     * 异步并行任务
     */
    fun serviceAsync(order: Int) = GlobalScope.async {
        blokingIoWork()
        order
    }


    /**
     * 模拟耗时的io操作
     */
    fun blokingIoWork() = runBlocking {
        delay(2)
    }
}