/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.ble

/**
 * @ Description:
 * @author king
 * @ Date 2020/9/1 16:58
 * @version V1.0
 */
object BleMsg {


    // Scan
    const val MSG_SCAN_DEVICE = 0X00

    // Connect
    const val MSG_CONNECT_FAIL = 0x01
    const val MSG_DISCONNECTED = 0x02
    const val MSG_RECONNECT = 0x03
    const val MSG_DISCOVER_SERVICES = 0x04
    const val MSG_DISCOVER_FAIL = 0x05
    const val MSG_DISCOVER_SUCCESS = 0x06
    const val MSG_CONNECT_OVER_TIME = 0x07

    // Notify
    const val MSG_CHA_NOTIFY_START = 0x11
    const val MSG_CHA_NOTIFY_RESULT = 0x12
    const val MSG_CHA_NOTIFY_DATA_CHANGE = 0x13
    const val KEY_NOTIFY_BUNDLE_STATUS = "notify_status"
    const val KEY_NOTIFY_BUNDLE_VALUE = "notify_value"

    // Indicate
    const val MSG_CHA_INDICATE_START = 0x21
    const val MSG_CHA_INDICATE_RESULT = 0x22
    const val MSG_CHA_INDICATE_DATA_CHANGE = 0x23
    const val KEY_INDICATE_BUNDLE_STATUS = "indicate_status"
    const val KEY_INDICATE_BUNDLE_VALUE = "indicate_value"

    // Write
    const val MSG_CHA_WRITE_START = 0x31
    const val MSG_CHA_WRITE_RESULT = 0x32
    const val MSG_SPLIT_WRITE_NEXT = 0x33
    const val KEY_WRITE_BUNDLE_STATUS = "write_status"
    const val KEY_WRITE_BUNDLE_VALUE = "write_value"

    // Read
    const val MSG_CHA_READ_START = 0x41
    const val MSG_CHA_READ_RESULT = 0x42
    const val KEY_READ_BUNDLE_STATUS = "read_status"
    const val KEY_READ_BUNDLE_VALUE = "read_value"

    // Rssi
    const val MSG_READ_RSSI_START = 0x51
    const val MSG_READ_RSSI_RESULT = 0x52
    const val KEY_READ_RSSI_BUNDLE_STATUS = "rssi_status"
    const val KEY_READ_RSSI_BUNDLE_VALUE = "rssi_value"

    // Mtu
    //最大传输单元，是指一种通信协议的某一层上面所能通过的最大数据报大小（以字节为单位）。而在Android BLE开发中，则指每包数据能携带的最大字节上限。
    const val MSG_SET_MTU_START = 0x61
    const val MSG_SET_MTU_RESULT = 0x62
    const val KEY_SET_MTU_BUNDLE_STATUS = "mtu_status"
    const val KEY_SET_MTU_BUNDLE_VALUE = "mtu_value"


}