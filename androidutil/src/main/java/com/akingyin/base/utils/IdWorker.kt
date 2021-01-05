/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.akingyin.base.utils

/**
 * Created by Administrator on 2017/9/16.
 */
class IdWorker private constructor(workerId: Long) {
    private val workerId: Long
    private val epoch = 1403854494756L // 时间起始标记点，作为基准，一般取系统的最近时间
    private val workerIdBits = 10L // 机器标识位数
    private val maxWorkerId = -1L xor -1L shl workerIdBits.toInt() // 机器ID最大值: 1023
    private var sequence = 0L // 0，并发控制
    private val sequenceBits = 12L //毫秒内自增位
    private val workerIdShift = sequenceBits // 12
    private val timestampLeftShift = sequenceBits + workerIdBits // 22
    private val sequenceMask = -1L xor -1L shl sequenceBits.toInt() // 4095,111111111111,12位
    private var lastTimestamp = -1L
    @Synchronized
    @Throws(Exception::class)
    fun nextId(): Long {
        var timestamp: Long = timeGen()
        if (lastTimestamp == timestamp) { // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环); 对新的timestamp，sequence从0开始
            sequence = sequence + 1 and sequenceMask
            if (sequence == 0L) {
                timestamp = tilNextMillis(lastTimestamp) // 重新生成timestamp
            }
        } else {
            sequence = 0
        }
        if (timestamp < lastTimestamp) {
            throw Exception(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", lastTimestamp - timestamp))
        }
        lastTimestamp = timestamp
        return timestamp - epoch shl timestampLeftShift.toInt() or (workerId shl workerIdShift.toInt()) or sequence
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp: Long = timeGen()
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen()
        }
        return timestamp
    }

    companion object {
        val flowIdWorkerInstance = IdWorker(1)

        /**
         * 获得系统当前毫秒数
         */
        private fun timeGen(): Long {
            return System.currentTimeMillis()
        }
    }

    init {
        require(!(workerId > maxWorkerId || workerId < 0)) { String.format("worker Id can't be greater than %d or less than 0", maxWorkerId) }
        this.workerId = workerId
    }
}