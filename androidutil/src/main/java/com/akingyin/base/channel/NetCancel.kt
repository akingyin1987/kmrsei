/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.channel

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/23 12:32
 * @version V1.0
 */
object NetCancel {


    private val map = mutableMapOf<Canceller, Any>()

    /**
     * Add a task to cancel.
     *
     * @param uid   target request.
     * @param canceller canceller.
     */
    fun add(uid: Any?, canceller: Canceller) {
        uid ?: return
        map[canceller] = uid
    }

    /**
     * Remove a task.
     *
     * @param uid target request.
     */
    fun remove(uid: Any?) {
        uid ?: return
        val iterator = map.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value == uid) iterator.remove()
        }
    }

    /**
     * According to the tag to cancel a task.
     *
     */
    fun cancel(uid: Any?) {
        uid ?: return
        val iterator = map.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (uid == next.value) {
                iterator.remove()
                next.key.cancel()
            }
        }
    }
}