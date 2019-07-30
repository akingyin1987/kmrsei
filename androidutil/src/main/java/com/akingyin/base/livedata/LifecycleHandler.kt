package com.akingyin.base.livedata

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/17 13:12
 * @version V1.0
 */
class LifecycleHandler(private val lifecycleOwner: LifecycleOwner?,
                       looper: Looper = Looper.getMainLooper()) : Handler(looper), LifecycleObserver {
    init {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        removeCallbacksAndMessages(null)
        lifecycleOwner?.lifecycle?.removeObserver(this)
    }

}