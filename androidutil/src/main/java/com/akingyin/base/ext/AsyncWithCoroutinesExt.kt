package com.akingyin.base.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

/**
 * @ Description:
 * @author king
 * @ Date 2020/1/7 11:54
 * @version V1.0
 */



internal class CoroutineLifecycleListener(private val deferred: Deferred<*>) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCoroutine() {
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
    }
}


// CoroutineContext running on background threads.


@ObsoleteCoroutinesApi
internal val Background = newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() * 2, "Loader")


/**
 * Creates a lazily started coroutine that runs <code>loader()</code>.
 * The coroutine is automatically cancelled using the CoroutineLifecycleListener.
 */
@ObsoleteCoroutinesApi
fun <T> LifecycleOwner.load(loader: suspend () -> T): Deferred<T> {

    val deferred = GlobalScope.async(context = Background, start = CoroutineStart.LAZY) {
        loader()
    }
    lifecycle.addObserver(CoroutineLifecycleListener(deferred))
    return deferred
}


/**
 * Extension function on <code>Deferred<T><code> that creates a launches a coroutine which
 * will call <code>await()</code> and pass the returned value to <code>block()</code>.
 */
infix fun <T> Deferred<T>.then(block: suspend (T) -> Unit): Job {
    return GlobalScope.launch(Main) {
        try {
            block(this@then.await())
        } catch (e: Exception) {
            // Just log the exception to confirm when we get cancelled (Expect JobCancellationException)
             e.printStackTrace()
            throw e
        }
    }
}