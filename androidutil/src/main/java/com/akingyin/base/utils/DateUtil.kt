package com.akingyin.base.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期类工具
 * @ Description:
 * @ Author king
 * @ Date 2017/2/22 17:19
 *
 * @ Version V1.0
 * 注意：SimpleDateFormat不是线程安全的，线程安全需用`ThreadLocal<SimpleDateFormat>`
 */
object DateUtil {
    const val DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss"
    const val HH_MM_SS_SSS = "HH:mm:ss SSS"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val HH_MM = "HH:mm"
    const val HH_MM_SS = "HH:mm:ss"
    private val threadLocal = ThreadLocal<SimpleDateFormat>()
    private val SDF_THREAD_LOCAL = ThreadLocal<SimpleDateFormat>()
    private val `object` = Any()
    /******************** 时间相关常量  */
    /**
     * 秒与毫秒的倍数
     */
    const val SEC = 1000

    /**
     * 分与毫秒的倍数
     */
    const val MIN = 60000

    /**
     * 时与毫秒的倍数
     */
    const val HOUR = 3600000

    /**
     * 天与毫秒的倍数
     */
    const val DAY = 86400000

    /**
     * 获取SimpleDateFormat
     *
     * @param pattern
     * 日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException
     * 异常：非法日期格式
     */
    @SuppressLint("SimpleDateFormat")
    @Throws(RuntimeException::class)
    private fun getDateFormat(pattern: String): SimpleDateFormat {
        var dateFormat = threadLocal.get()
        if (dateFormat == null) {
            synchronized(`object`) {
                if (dateFormat == null) {
                    dateFormat = SimpleDateFormat(pattern).apply {
                        isLenient = false
                    }
                    threadLocal.set(dateFormat)
                }
            }
        }
        dateFormat?.applyPattern(pattern)
        return dateFormat!!
    }

    private val defaultFormat: SimpleDateFormat
        get() {
            var simpleDateFormat = SDF_THREAD_LOCAL.get()
            if (simpleDateFormat == null) {
                simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                SDF_THREAD_LOCAL.set(simpleDateFormat)
            }
            return simpleDateFormat
        }

    /**
     * 将时间戳转为时间字符串
     * @param millis
     * @return
     */
    fun millis2String(millis: Long?, simpleDateFormat: SimpleDateFormat): String {
        return if (null == millis) {
            StringUtils.DEFAULT_EMPTY
        } else simpleDateFormat.format(Date(millis))
    }

    /**
     * 将时间戳转为时间字符串
     * @param millis
     * @return
     */
    fun millis2String(millis: Long?): String {
        return if (null == millis || millis <= 0L) {
            StringUtils.DEFAULT_EMPTY
        } else defaultFormat.format(Date(millis))
    }

    /**
     * 将时间转为指定格式字符串
     * @param millis
     * @param pattern
     * @return
     */
    @JvmStatic
    fun millis2String(millis: Long?, pattern: String): String {
        return if (null == millis) {
            StringUtils.DEFAULT_EMPTY
        } else getDateFormat(pattern).format(Date(millis))
    }

    /**
     * 将时间字符串转为时间戳
     *
     * time格式为pattern
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return 毫秒时间戳
     */
    @JvmOverloads
    fun string2Millis(time: String?, pattern: String? = DEFAULT_PATTERN): Long {
        try {
            return SimpleDateFormat(pattern, Locale.getDefault()).parse(time).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return -1
    }
    /**
     * 将时间字符串转为Date类型
     *
     * time格式为pattern
     *
     * @return Date类型
     */
    /**
     * 将时间字符串转为Date类型
     *
     * time格式为yyyy-MM-dd HH:mm:ss
     *
     * @param time 时间字符串
     * @return Date类型
     */
    @JvmOverloads
    fun string2Date(time: String?, pattern: String? = DEFAULT_PATTERN): Date {
        return Date(string2Millis(time, pattern))
    }
    /**
     * 将Date类型转为时间字符串
     *
     * 格式为pattern
     *
     * @return 时间字符串
     */
    /**
     * 将Date类型转为时间字符串
     *
     * 格式为yyyy-MM-dd HH:mm:ss
     *
     * @param date Date类型时间
     * @return 时间字符串
     */
    @JvmOverloads
    fun date2String(date: Date, pattern: String? = DEFAULT_PATTERN): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    /**
     * 将Date类型转为时间戳
     *
     * @param date Date类型时间
     * @return 毫秒时间戳
     */
    fun date2Millis(date: Date): Long {
        return date.time
    }

    /**
     * 将时间戳转为Date类型
     *
     * @param millis 毫秒时间戳
     * @return Date类型时间
     */
    fun millis2Date(millis: Long): Date {
        return Date(millis)
    }

    /**
     * 获取两个时间差（单位：unit）
     *
     * time0和time1格式都为yyyy-MM-dd HH:mm:ss
     *
     * @param time0 时间字符串0
     * @param time1 时间字符串1
     * @param unit  单位类型
     *
     * @return unit时间戳
     */
    fun getTimeSpan(time0: String?, time1: String?, unit: TimeUnit?): Long {
        return getTimeSpan(time0, time1, unit, DEFAULT_PATTERN)
    }

    /**
     * 获取两个时间差（单位：unit）
     *
     * time0和time1格式都为format
     *
     * @param time0   时间字符串0
     * @param time1   时间字符串1
     * @param unit    单位类型
     *
     * @param pattern 时间格式
     * @return unit时间戳
     */
    fun getTimeSpan(time0: String?, time1: String?, unit: TimeUnit?, pattern: String?): Long {
        return millis2TimeSpan(Math.abs(string2Millis(time0, pattern) - string2Millis(time1, pattern)), unit)
    }

    /**
     * 获取两个时间差（单位：unit）
     *
     * @param date0 Date类型时间0
     * @param date1 Date类型时间1
     * @param unit  单位类型
     *
     * @return unit时间戳
     */
    fun getTimeSpan(date0: Date, date1: Date, unit: TimeUnit?): Long {
        return millis2TimeSpan(Math.abs(date2Millis(date0) - date2Millis(date1)), unit)
    }

    /**
     * 毫秒时间戳转以unit为单位的时间长度
     *
     * @param millis 毫秒时间戳
     * @param unit   单位类型
     *
     * @return 以unit为单位的时间长度
     */
    fun millis2TimeSpan(millis: Long, unit: TimeUnit?): Long {
        return when (unit) {
            TimeUnit.MSEC -> millis
            TimeUnit.SEC -> millis / SEC
            TimeUnit.MIN -> millis / MIN
            TimeUnit.HOUR -> millis / HOUR
            TimeUnit.DAY -> millis / DAY
            else -> 0L
        }
    }

    /**
     * 获取当前毫秒时间戳
     *
     * @return 毫秒时间戳
     */
    val nowTimeMills: Long
        get() = System.currentTimeMillis()

    /**
     * 获取当前时间字符串
     *
     * 格式为yyyy-MM-dd HH:mm:ss
     *
     * @return 时间字符串
     */
    @JvmStatic
    val nowTimeString: String
        get() = millis2String(System.currentTimeMillis(), DEFAULT_PATTERN)

    /**
     * 获取当前时间字符串
     *
     * 格式为pattern
     *
     * @param pattern 时间格式
     * @return 时间字符串
     */
    fun getNowTimeString(pattern: String): String {
        return millis2String(System.currentTimeMillis(), pattern)
    }

    /**
     * 获取友好型与当前时间的差
     *
     * time格式为yyyy-MM-dd HH:mm:ss
     *
     * @param time 时间字符串
     * @return 友好型与当前时间的差
     *
     *  * 如果小于1秒钟内，显示刚刚
     *  * 如果在1分钟内，显示XXX秒前
     *  * 如果在1小时内，显示XXX分钟前
     *  * 如果在1小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    fun getFriendlyTimeSpanByNow(time: String?): String {
        return getFriendlyTimeSpanByNow(time, DEFAULT_PATTERN)
    }

    fun getFriendlyTimeSpanByNow(time: String?, pattern: String?): String {
        return getFriendlyTimeSpanByNow(string2Millis(time, pattern))
    }

    fun getFriendlyTimeSpanByNow(date: Date): String {
        return getFriendlyTimeSpanByNow(date.time)
    }

    /**
     * 获取友好型与当前时间的差
     *
     * @param millis 毫秒时间戳
     * @return 友好型与当前时间的差
     *
     *  * 如果小于1秒钟内，显示刚刚
     *  * 如果在1分钟内，显示XXX秒前
     *  * 如果在1小时内，显示XXX分钟前
     *  * 如果在1小时外的今天内，显示今天15:32
     *  * 如果是昨天的，显示昨天15:32
     *  * 其余显示，2016-10-15
     *  * 时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     *
     */
    @SuppressLint("DefaultLocale")
    fun getFriendlyTimeSpanByNow(millis: Long): String {
        val now = System.currentTimeMillis()
        val span = now - millis
        if (span < 0) {
            return String.format("%tc",
                    millis) // U can read http://www.apihome.cn/api/java/Formatter.html to understand it.
        }
        if (span < 1000) {
            return "刚刚"
        } else if (span < MIN) {
            return String.format("%d秒前", span / SEC)
        } else if (span < HOUR) {
            return String.format("%d分钟前", span / MIN)
        }
        // 获取当天00:00
        val wee = startOfTodDay()
        return if (millis >= wee) {
            String.format("今天%tR", millis)
        } else if (millis >= wee - DAY) {
            String.format("昨天%tR", millis)
        } else {
            String.format("%tF", millis)
        }
    }

    fun getCurrentFriendDay(time: Long?): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time ?: System.currentTimeMillis()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        var range = "凌晨"
        when (hour) {
            in 6..10 -> {
                range = "早晨"
            }
            in 11..13 -> {
                range = "中午"
            }
            in 14..18 -> {
                range = "下午"
            }
            in 19..23 -> {
                range = "晚上"
            }
        }
        return range
    }

    /**
     * 当天的开始时间
     * @return
     */
    fun startOfTodDay(): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val date = calendar.time
        return date.time
    }

    fun startOfTodDay(currentTime: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val date = calendar.time
        return date.time
    }

    /**
     * 当天的结束时间
     * @return
     */
    fun endOfTodDay(): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 999
        val date = calendar.time
        return date.time
    }

    fun startOfThisWeek(): Long { // 当周开始时间
        val currentDate = Calendar.getInstance()
        currentDate.firstDayOfWeek = Calendar.MONDAY
        currentDate[Calendar.HOUR_OF_DAY] = 0
        currentDate[Calendar.MINUTE] = 0
        currentDate[Calendar.SECOND] = 0
        currentDate[Calendar.MILLISECOND] = 0
        currentDate[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        val date = currentDate.time
        return date.time
    }

    /**
     * 功能：获取本周的结束时间 示例：2013-05-19 23:59:59
     */
    fun endOfThisWeek(): Long { // 当周结束时间
        val currentDate = Calendar.getInstance()
        currentDate.firstDayOfWeek = Calendar.MONDAY
        currentDate[Calendar.HOUR_OF_DAY] = 23
        currentDate[Calendar.MINUTE] = 59
        currentDate[Calendar.SECOND] = 59
        currentDate[Calendar.MILLISECOND] = 999
        currentDate[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY
        val date = currentDate.time
        return date.time
    }

    /**
     * 功能：获取本月的开始时间
     */
    fun startOfThisMonth(): Long { // 当周开始时间
        val currentDate = Calendar.getInstance()
        currentDate[Calendar.HOUR_OF_DAY] = 0
        currentDate[Calendar.MINUTE] = 0
        currentDate[Calendar.SECOND] = 0
        currentDate[Calendar.MILLISECOND] = 0
        currentDate[Calendar.DAY_OF_MONTH] = 1
        val date = currentDate.time
        return date.time
    }

    /**
     * 本月结束时间
     * @return
     */
    fun endOfThisMonth(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = 1
        cal[Calendar.HOUR_OF_DAY] = 23
        cal[Calendar.MINUTE] = 59
        cal[Calendar.SECOND] = 59
        cal[Calendar.MILLISECOND] = 999
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.DATE, -1)
        val date = cal.time
        return date.time
    }

    fun formatDurationTime(duration: Long): String {
        return String.format(Locale.getDefault(), "%02d:%02d",
                java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(duration), java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(duration)
                - java.util.concurrent.TimeUnit.MINUTES.toSeconds(
                java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(duration)))
    }

    enum class TimeUnit {
        MSEC, SEC, MIN, HOUR, DAY
    }
}