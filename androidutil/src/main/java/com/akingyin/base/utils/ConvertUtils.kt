package com.akingyin.base.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import com.akingyin.base.utils.CloseUtils.closeIO
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

/**
 * 数据转换工具类
 * @ Description:
 * @ Author king
 * @ Date 2017/2/28 10:52
 * @ Version V1.0
 */
class ConvertUtils private constructor() {
    companion object {
        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        /**
         * KB与Byte的倍数
         */
        const val KB = 1024

        /**
         * MB与Byte的倍数
         */
        const val MB = 1048576

        /**
         * GB与Byte的倍数
         */
        const val GB = 1073741824

        /**
         * short array to byte array.
         *
         * @param sData
         * the s data
         * @return the byte []
         */
        fun short2byte(sData: ShortArray): ByteArray {
            val shortArrsize = sData.size
            val bytes = ByteArray(shortArrsize * 2)
            for (i in 0 until shortArrsize) {
                bytes[i * 2] = (sData[i] and 0x00FF).toByte()
                bytes[i * 2 + 1] = (sData[i].toInt() shr 8).toByte()
                sData[i] = 0
            }
            return bytes
        }

        /**
         * 短整型与字节的转换
         */
        fun shortToByte(number: Short): ByteArray {
            var temp = number.toInt()
            val b = ByteArray(2)
            for (i in b.indices) {
                // 将最低位保存在最低位
                b[i] = (temp and 0xff.toByte().toInt()).toByte()
                // 向右移8位
                temp = temp shr 8
            }
            return b
        }

        fun encodeBytes(source: ByteArray, split: Char): ByteArray {
            val bos = ByteArrayOutputStream(source.size)
            for (b in source) {
                var sourceValue = b
                if (b < 0) {
                  sourceValue= (b + 256).toByte()
                }
                bos.write(split.toInt())
                val hex1 = Character.toUpperCase(Character.forDigit(sourceValue.toInt() shr 4 and 0xF, 16))
                val hex2 = Character.toUpperCase(Character.forDigit((sourceValue and 0xF).toInt(), 16))
                bos.write(hex1.toInt())
                bos.write(hex2.toInt())
            }
            return bos.toByteArray()
        }

        /**
         * byteArr转hexString
         *
         * 例如：
         * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
         *
         * @param src 字节数组
         * @return 16进制大写字符串
         */
        fun bytesToHexString(src: ByteArray): String {
            val stringBuilder = StringBuilder("")

            for (i in src.indices) {
                val v: Byte = src[i] and 0xFF.toByte()
                val hv = Integer.toHexString(v.toInt())
                if (hv.length < 2) {
                    stringBuilder.append(0)
                }
                stringBuilder.append(hv)
            }
            return stringBuilder.toString().toUpperCase(Locale.ROOT)
        }

        /**
         * 倒序排列
         * @param src
         * @return
         */
        fun bytes2HexStrReverse(src: ByteArray): String {
            val stringBuilder = StringBuilder("")

            for (i in src.indices.reversed()) {
                val v: Byte = src[i] and 0xFF.toByte()
                val hv = Integer.toHexString(v.toInt())
                if (hv.length < 2) {
                    stringBuilder.append(0)
                }
                stringBuilder.append(hv)
            }
            return stringBuilder.toString().toUpperCase(Locale.ROOT)
        }

        /**
         * hexString转byteArr
         *
         * 例如：
         * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
         *
         * @param hexString 十六进制字符串
         * @return 字节数组
         */
        fun hexString2Bytes(hexString: String): ByteArray? {
            var hexString = hexString
            if (StringUtils.isEmpty(hexString)) {
                return null
            }
            var len = hexString.length
            if (len % 2 != 0) {
                hexString = "0$hexString"
                len += 1
            }
            val hexBytes = hexString.toUpperCase(Locale.ROOT).toCharArray()
            val ret = ByteArray(len shr 1)
            var i = 0
            while (i < len) {
                ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
                i += 2
            }
            return ret
        }

        /**
         * hexChar转int
         *
         * @param hexChar hex单个字节
         * @return 0..15
         */
        private fun hex2Dec(hexChar: Char): Int {
            return if (hexChar in '0'..'9') {
                hexChar - '0'
            } else if (hexChar in 'A'..'F') {
                hexChar - 'A' + 10
            } else {
                throw IllegalArgumentException()
            }
        }

        /**
         * inputStream转outputStream
         *
         * @param is 输入流
         * @return outputStream子类
         */
        fun input2OutputStream(`is`: InputStream?): ByteArrayOutputStream? {
            return if (`is` == null) {
                null
            } else try {
                val os = ByteArrayOutputStream()
                val b = ByteArray(KB)
                var len: Int
                while (`is`.read(b, 0, KB).also { len = it } != -1) {
                    os.write(b, 0, len)
                }
                os
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                closeIO(`is`)
            }
        }

        /**
         * 字节数转合适内存大小
         *
         * 保留3位小数
         *
         * @param byteNum 字节数
         * @return 合适内存大小
         */
        fun LongToFitMemorySize(byteNum: Long): String {
            return when {
                byteNum < 0 -> {
                    "shouldn't be less than zero!"
                }
                byteNum < KB -> {
                    String.format(Locale.getDefault(), "%.1fB", byteNum * 1f)
                }
                byteNum < MB -> {
                    String.format(Locale.getDefault(), "%.1fKB", byteNum / KB * 1f)
                }
                byteNum < GB -> {
                    String.format(Locale.getDefault(), "%.1fMB", byteNum / MB * 1f)
                }
                else -> {
                    String.format(Locale.getDefault(), "%.1fGB", byteNum / GB + 0.0005)
                }
            }
        }

        /**
         * Dp to pixels
         *
         * @param context
         * the context
         * @param dp
         * the dp
         * @return the int
         */
        fun dpToPixels(context: Activity, dp: Int): Int {
            val metrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metrics)
            return (dp * metrics.density + 0.5).toInt()
        }

        /**
         * Pixels to dp int.
         *
         * @param context
         * the context
         * @param px
         * the px
         * @return the int
         */
        fun pixelsToDp(context: Activity, px: Int): Int {
            val metrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metrics)
            return (px / metrics.density + 0.5).toInt()
        }

        /**
         * Sp to px
         *
         * @param context
         * the context
         * @param sp
         * the sp
         * @return the int
         */
        fun spToPx(context: Context, sp: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (sp * fontScale + 0.5f).toInt()
        }

        /**
         * Px to sp
         *
         * @param context
         * the context
         * @param px
         * the px
         * @return the int
         */
        fun pxToSp(context: Context, px: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (px / fontScale + 0.5f).toInt()
        }

        // 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
        fun MD5(md5: String): String? {
            try {
                val md = MessageDigest
                        .getInstance("MD5")
                val array = md.digest(md5.toByteArray())
                val sb = StringBuffer()
                for (i in array.indices) {
                    sb.append((Integer.toHexString(array[i].toInt() and 0xFF)).let {
                        if(it.length == 1){
                            it+"0"
                        }else{
                            it
                        }
                    })
                }
                return sb.toString()
            } catch (e: NoSuchAlgorithmException) {
            }
            return null
        }
    }


}