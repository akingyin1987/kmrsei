/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble.callback

import android.os.Handler
import com.akingyin.base.ble.exception.BleException

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/3 14:18
 * @version V1.0
 */
abstract class BleWriteCallback : BleBaseCallback(){


    abstract fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray)

    abstract fun onWriteFailure(exception: BleException)
}