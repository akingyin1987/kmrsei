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

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Base64
import com.akingyin.base.ext.app
import timber.log.Timber
import java.io.*

/**
 * @ Description:
 *
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/5/25 18:22
 * @ Version V1.0
 */
object PreferencesUtil {
    private const val DefString = ""
    private const val DefFloat = 0f
    private const val DefInt = 0
    private const val DefBoolean = false

    @JvmStatic
    var defaultName = "app_setting"


    private fun getPreferences(name: String): SharedPreferences {
        return app.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    operator fun get(key: String, defValue: Boolean): Boolean {
        return get(defaultName, key, defValue)
    }

    fun getBoolean(key: String): Boolean {
        return get(defaultName, key, DefBoolean)
    }

    operator fun get(key: String, defValue: Int): Int {
        return get(defaultName, key, defValue)
    }

    fun getInt(key: String): Int {
        return get(defaultName, key, DefInt)
    }

    operator fun get(key: String, defValue: Float): Float {
        return get(defaultName, key, defValue)
    }

    fun getFloat(key: String): Float {
        return get(defaultName, key, DefFloat)
    }

    operator fun get(key: String, defValue: Long): Long {
        return get(defaultName, key, defValue)
    }

    fun getString(key: String): String ?{
        return get(defaultName, key, DefString)
    }

    operator fun get(key: String, defValue: String): String? {
        return get(defaultName, key, defValue)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    operator fun get(key: String, defValue: Set<String?>?): Set<String>? {
        return get(defaultName, key, defValue)
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    operator fun <C : Serializable?> get(key: String, defValue: C): C {
        return get(defaultName, key, defValue)
    }

     fun get(name: String, key: String, defValue: Boolean): Boolean {
        return getPreferences(name).getBoolean(key, defValue)
    }

     fun get(name: String, key: String, defValue: Int): Int {
        return getPreferences(name).getInt(key, defValue)
    }

    operator fun get(name: String, key: String, defValue: Float): Float {
        return getPreferences(name).getFloat(key, defValue)
    }

    operator fun get(name: String, key: String, defValue: Long): Long {
        return getPreferences(name).getLong(key, defValue)
    }

     fun get(name: String, key: String, defValue: String): String {
        return getPreferences(name).getString(key, defValue)?:defValue
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    operator fun get(name: String, key: String, defValue: Set<String?>?): Set<String>? {
        return getPreferences(name).getStringSet(key, defValue)
    }

    @Suppress("UNCHECKED_CAST")
    @TargetApi(Build.VERSION_CODES.FROYO)
    operator fun <C : Serializable?> get(name: String, key: String, defValue: C): C {
        var bais: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        var result = defValue
        val value = getPreferences(name).getString(key, null)
        if (value != null) {
            try {
                val decoded = Base64.decode(value.toByteArray(), Base64.DEFAULT)
                bais = ByteArrayInputStream(decoded)
                ois = ObjectInputStream(bais)
                result = ois.readObject() as C
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                if (ois != null) {
                    try {
                        ois.close()
                    } catch (e: IOException) {
                        Timber.e(e)
                    }
                }
                if (bais != null) {
                    try {
                        bais.close()
                    } catch (e: IOException) {
                        Timber.e(e)
                    }
                }
            }
        }
        return result
    }

    fun put(key: String, value: Boolean) {
        put(defaultName, key, value)
    }

    fun put(key: String, value: Int) {
        put(defaultName, key, value)
    }

    fun put(key: String, value: Float) {
        put(defaultName, key, value)
    }

    fun put(key: String, value: Long) {
        put(defaultName, key, value)
    }

    fun put(key: String, value: String) {
        put(defaultName, key, value)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun put(key: String, value: Set<String>) {
        put(defaultName, key, value)
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    fun <C : Serializable?> put(key: String, value: C) {
        put(defaultName, key, value)
    }

    fun put(name: String, key: String, value: Boolean) {
        getPreferences(name).edit().putBoolean(key, value).apply()
    }

    fun put(name: String, key: String, value: Int) {
        getPreferences(name).edit().putInt(key, value).apply()
    }

    fun put(name: String, key: String, value: Float) {
        getPreferences(name).edit().putFloat(key, value).apply()
    }

    fun put(name: String, key: String, value: Long) {
        getPreferences(name).edit().putLong(key, value).apply()
    }

    fun put(name: String, key: String, value: String) {
        getPreferences(name).edit().putString(key, value).apply()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun put(name: String, key: String, value: Set<String?>) {
        getPreferences(name).edit().putStringSet(key, value).apply()
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    fun <C : Serializable?> put(name: String, key: String, value: C) {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(value)
            val encoded = Base64.encode(baos.toByteArray(), Base64.DEFAULT)
            getPreferences(name).edit().putString(key, String(encoded)).apply()
        } catch (e: IOException) {

            throw RuntimeException(e)
        } finally {
            if (oos != null) {
                try {
                    oos.close()
                } catch (e: IOException) {

                }
            }
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {

                }
            }
        }
    }

    fun remove(key: String) {
        remove(defaultName, key)
    }

    fun remove(name: String, key: String) {
        getPreferences(name).edit().remove(key).apply()
    }

    @JvmOverloads
    fun clear(name: String = defaultName) {
        getPreferences(name).edit().clear().apply()
    }
}