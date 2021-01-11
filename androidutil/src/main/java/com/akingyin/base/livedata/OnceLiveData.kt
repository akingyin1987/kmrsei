/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 *  只执行一次的LiveData
 * @ Description:
 * @author king
 * @ Date 2021/1/11 11:24
 * @version V1.0
 */
class OnceLiveData<T> : MutableLiveData<T>() {

    private var isRead: AtomicBoolean = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, {
            if (isRead.compareAndSet(false, true)) {
                observer.onChanged(it)
            }
        })
    }

    override fun postValue(value: T) {
        isRead.set(false)
        super.postValue(value)
    }

    override fun setValue(value: T) {
        isRead.set(false)
        super.setValue(value)
    }
}