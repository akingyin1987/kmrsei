/*
 *
 *   Copyright (c) 2016 [akingyin@163.com]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License”);
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.akingyin.base.utils

import android.content.Context
import java.lang.ref.WeakReference
import java.security.InvalidParameterException

/**
 * @author king
 * @version V1.0
 * @ Description:
 * 保存context 上下文
 * Company:重庆中陆承大科技有限公司
 */
object KissTools {
    const val TAG = "KissTools"
    private var contextRef: WeakReference<Context>? = null
    fun setContext(context: Context) {

        val appContext = context.applicationContext
        contextRef = WeakReference(appContext)
    }

    @JvmStatic
    val applicationContext: Context
        get() {
            val context = contextRef?.get()
            return context ?: throw InvalidParameterException("Context parameter not set!")
        }
}