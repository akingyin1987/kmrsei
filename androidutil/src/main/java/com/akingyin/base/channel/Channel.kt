/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */


@file:Suppress("ObjectPropertyName", "EXPERIMENTAL_API_USAGE")
package com.akingyin.base.channel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

import kotlinx.coroutines.runBlocking

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/23 12:07
 * @version V1.0
 */
@ExperimentalCoroutinesApi
val _channel = BroadcastChannel<Bus<Any>>(Channel.BUFFERED)

// <editor-fold desc="发送">

@ExperimentalCoroutinesApi
fun sendEvent(event: Any, tag: String = "") =
        runBlocking { _channel.send(Bus(event, tag)) }


fun sendTag(tag: String) = runBlocking { _channel.send(Bus(TagEvent(), tag)) }

// </editor-fold>


// <editor-fold desc="接收">

inline fun <reified T> LifecycleOwner.receiveEvent(
        active: Boolean = false,
        vararg tags: String = arrayOf(),
        lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
        noinline block: suspend CoroutineScope.(event: T) -> Unit
): ChannelScope {

    val coroutineScope = ChannelScope(this, lifecycleEvent)

    return coroutineScope.launch {
        for (bus in _channel.openSubscription()) {
            if (bus.event is T && (tags.isEmpty() && bus.tag.isBlank() || tags.contains(bus.tag))) {
                if (active) {
                    MutableLiveData<T>().apply {
                        observe(this@receiveEvent, Observer {
                            coroutineScope.launch {
                                block(it)
                            }
                        })
                        value = bus.event
                    }
                } else block(bus.event)
            }
        }
    }
}


inline fun <reified T> receiveEvent(
        vararg tags: String = arrayOf(),
        noinline block: suspend (event: T) -> Unit
): ChannelScope {
    val coroutineScope = ChannelScope()

    return coroutineScope.launch {
        for (bus in _channel.openSubscription()) {
            if (bus.event is T && (tags.isEmpty() && bus.tag.isBlank() || tags.contains(bus.tag))) {
                block(bus.event)
            }
        }
    }
}


fun LifecycleOwner.receiveTag(
        active: Boolean = false,
        vararg tags: String,
        lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
        block: suspend CoroutineScope.(tag: String) -> Unit
): ChannelScope {

    val coroutineScope = ChannelScope(this, lifecycleEvent)

    return coroutineScope.launch {
        for (bus in _channel.openSubscription()) {
            if (bus.event is TagEvent && tags.contains(bus.tag)) {
                if (active) {
                    MutableLiveData<String>().apply {
                        observe(this@receiveTag, Observer {
                            coroutineScope.launch {
                                block(it)
                            }
                        })
                        value = bus.tag
                    }
                } else block(bus.tag)
            }
        }
    }

}

fun receiveTag(
        vararg tags: String,
        block: suspend CoroutineScope.(tag: String) -> Unit
): ChannelScope {

    val coroutineScope = ChannelScope()

    return coroutineScope.launch {
        for (bus in _channel.openSubscription()) {
            if (bus.event is TagEvent && tags.contains(bus.tag)) {
                block(bus.tag)
            }
        }
    }
}