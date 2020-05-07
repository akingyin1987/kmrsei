package com.akingyin.base.mvvm



import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean


/**
 * @ Description:
 * @author king
 * @ Date 2020/1/11 12:07
 * @version V1.0
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val TAG = "SingleLiveEvent"

    private val mPending: AtomicBoolean = AtomicBoolean(false)


    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Timber.tag(TAG).w("Multiple observers registered but only one will be notified of changes.");
        }
        super.observe(owner, Observer {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }




    @MainThread
    override fun setValue(@Nullable t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }
}