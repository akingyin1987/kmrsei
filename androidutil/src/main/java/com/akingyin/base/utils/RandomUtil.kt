package com.akingyin.base.utils

import java.util.*

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/9 13:52
 */
object RandomUtil {
    val randomUUID: String
        get() = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT)

    /**
    * 生成随机字符串
     *
     * @param length 生成字符串的长度
     * @return
     */
    @JvmStatic
    fun getRandomString(length: Int): String {
        val base = "abcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        val sb = StringBuffer()
        for (i in 0 until length) {
            val number = random.nextInt(base.length)
            sb.append(base[number])
        }
        return sb.toString()
    }

    /**
     * 获取随机整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    fun getRandomNum(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1) + min
    }
}