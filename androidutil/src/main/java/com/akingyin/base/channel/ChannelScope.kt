/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.channel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/23 12:03
 * @version V1.0
 */
open class ChannelScope() :CoroutineScope {

    constructor(
            lifecycleOwner: LifecycleOwner,
            lifeEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
    ) : this() {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (lifeEvent == event) {
                    cancel()
                }
            }
        })
    }


    protected var catch: (ChannelScope.(Throwable) -> Unit)? = null
    protected var finally: (ChannelScope.(Throwable?) -> Unit)? = null
    protected var auto = true

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        catch(throwable)
    }

    override val coroutineContext: CoroutineContext =
            Dispatchers.Main + exceptionHandler + SupervisorJob()


    open fun launch(
            block: suspend CoroutineScope.() -> Unit
    ): ChannelScope {
        start()
        launch(EmptyCoroutineContext, block = block).invokeOnCompletion { finally(it) }
        return this
    }

    protected open fun start() {

    }

    protected open fun catch(e: Throwable) {
        catch?.invoke(this, e) ?: handleError(e)
    }

    protected open fun finally(e: Throwable?) {
        finally?.invoke(this, e)
    }

    /**
     * 当作用域内发生异常时回调
     */
    open fun catch(block: ChannelScope.(Throwable) -> Unit = {}): ChannelScope {
        this.catch = block
        return this
    }

    /**
     * 无论正常或者异常结束都将最终执行
     */
    open fun finally(block: ChannelScope.(Throwable?) -> Unit = {}): ChannelScope {
        this.finally = block
        return this
    }


    /**
     * 错误处理
     */
    open fun handleError(e: Throwable) {
        e.printStackTrace()
    }

    fun autoOff() {
        auto = false
    }
}