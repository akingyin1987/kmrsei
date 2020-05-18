package com.akingyin.map.base

import android.util.Log
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/18 11:20
 * @version V1.0
 */
class Weak<T : Any>(initializer: () -> T?) {
    var weakReference = WeakReference<T?>(initializer())

    constructor():this({
        null
    })

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        Log.d("Weak Delegate","-----------getValue")
        return weakReference.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        Log.d("Weak Delegate","-----------setValue")
        weakReference = WeakReference(value)
    }

}