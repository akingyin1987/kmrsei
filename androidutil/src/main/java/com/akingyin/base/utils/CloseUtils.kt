package com.akingyin.base.utils

import java.io.Closeable
import java.io.IOException

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/2/28 11:56
 * @ Version V1.0
 */
object CloseUtils {
    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    @JvmStatic
    fun closeIO(vararg closeables: Closeable) {

        for (closeable in closeables) {
            try {
                closeable.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeable
     */
    fun closeIOQuietly(vararg closeables: Closeable) {

        for (closeable in closeables) {
            try {
                closeable.close()
            } catch (ignored: IOException) {
            }
        }
    }
}