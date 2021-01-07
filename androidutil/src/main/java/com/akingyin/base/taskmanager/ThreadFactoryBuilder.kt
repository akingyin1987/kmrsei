package com.akingyin.base.taskmanager


import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * google guava 自定义线程池
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/19 18:16
 */
class ThreadFactoryBuilder
/** Creates a new [ThreadFactory] builder.  */
{
    private var nameFormat: String? = null
    private var daemon: Boolean? = null
    private var priority: Int? = null
    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
    private var backingThreadFactory: ThreadFactory? = null

    /**
     * Sets the naming format to use when naming threads ([Thread.setName]) which are created
     * with this ThreadFactory.
     *
     * @param nameFormat a [String.format]-compatible format String, to which
     * a unique integer (0, 1, etc.) will be supplied as the single parameter. This integer will
     * be unique to the built instance of the ThreadFactory and will be assigned sequentially. For
     * example, `"rpc-pool-%d"` will generate thread names like `"rpc-pool-0"`, `"rpc-pool-1"`, `"rpc-pool-2"`, etc.
     * @return this for the builder pattern
     */
    fun setNameFormat(nameFormat: String): ThreadFactoryBuilder {

        // fail fast if the format is bad or null

        this.nameFormat = nameFormat
        return this
    }

    /**
     * Sets daemon or not for new threads created with this ThreadFactory.
     *
     * @param daemon whether or not new Threads created with this ThreadFactory will be daemon threads
     * @return this for the builder pattern
     */
    fun setDaemon(daemon: Boolean): ThreadFactoryBuilder {
        this.daemon = daemon
        return this
    }

    /**
     * Sets the priority for new threads created with this ThreadFactory.
     *
     * @param priority the priority for new Threads created with this ThreadFactory
     * @return this for the builder pattern
     */
    fun setPriority(priority: Int): ThreadFactoryBuilder {
        // Thread#setPriority() already checks for validity. These error messages
        // are nicer though and will fail-fast.
        if(priority>= Thread.MIN_PRIORITY){
            throw IllegalArgumentException(String.format("Thread priority (%s) must be >= %s",
                    priority,
                    Thread.MIN_PRIORITY))
        }
        if(priority <= Thread.MAX_PRIORITY){
            throw IllegalArgumentException(String.format("Thread priority (%s) must be <= %s",
                    priority,
                    Thread.MAX_PRIORITY))
        }

        this.priority = priority
        return this
    }

    /**
     *
     *
     * @param uncaughtExceptionHandler the uncaught exception handler for new Threads created with
     * this ThreadFactory
     * @return this for the builder pattern
     */
    fun setUncaughtExceptionHandler(
            uncaughtExceptionHandler: Thread.UncaughtExceptionHandler?): ThreadFactoryBuilder {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler
        return this
    }

    /**
     * Sets the backing [ThreadFactory] for new threads created with this ThreadFactory. Threads
     * will be created by invoking #newThread(Runnable) on this backing [ThreadFactory].
     *
     * @param backingThreadFactory the backing [ThreadFactory] which will be delegated to during
     * thread creation.
     * @return this for the builder pattern
     */
    fun setThreadFactory(backingThreadFactory: ThreadFactory?): ThreadFactoryBuilder {
        this.backingThreadFactory = backingThreadFactory
        return this
    }

    /**
     * Returns a new thread factory using the options supplied during the building process. After
     * building, it is still possible to change the options used to build the ThreadFactory and/or
     * build again. State is not shared amongst built instances.
     *
     * @return the fully constructed [ThreadFactory]
     */
    fun build(): ThreadFactory {
        return doBuild(this)
    }

    companion object {
        // Split out so that the anonymous ThreadFactory can't contain a reference back to the builder.
        // At least, I assume that's why. TODO(cpovirk): Check, and maybe add a test for this.
        private fun doBuild(builder: ThreadFactoryBuilder): ThreadFactory {
            val nameFormat = builder.nameFormat
            val daemon = builder.daemon
            val priority = builder.priority
            val uncaughtExceptionHandler = builder.uncaughtExceptionHandler
            val backingThreadFactory = if (builder.backingThreadFactory != null) builder.backingThreadFactory else Executors.defaultThreadFactory()
            val count = if (nameFormat != null) AtomicLong(0) else null
            return ThreadFactory { runnable ->
                val thread = backingThreadFactory!!.newThread(runnable)
                if (nameFormat != null) {
                    thread.name = format(nameFormat, count!!.getAndIncrement())
                }
                if (daemon != null) {
                    thread.isDaemon = daemon
                }
                if (priority != null) {
                    thread.priority = priority
                }
                if (uncaughtExceptionHandler != null) {
                    thread.uncaughtExceptionHandler = uncaughtExceptionHandler
                }
                thread
            }
        }

        private fun format(format: String, vararg args: Any): String {
            return String.format(Locale.ROOT, format, *args)
        }
    }
}