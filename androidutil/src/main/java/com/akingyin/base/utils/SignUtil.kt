package com.akingyin.base.utils

import com.akingyin.base.utils.RandomUtil.getRandomString
import com.blankj.utilcode.util.EncryptUtils
import java.util.*

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/9 13:50
 */
object SignUtil {
    private const val DEFAULT_SECRET = "FF10E31A-D695-4697-AA0E-D2E9BECADCC9"


    /**
     * 生成随机密钥
     *
     * @param length 长度
     * @return
     */
      @JvmStatic
      @JvmOverloads
     fun createRandomKeySecret(length: Int = 16): String {
        return getRandomString(length).toUpperCase(Locale.ROOT)
    }

    /**
     * 判断签名是否相等(默认secret的情况)
     *
     * @param sign
     * @param map
     * @return
     */
    fun checkSign(sign: String, map: SortedMap<Any, Any>): Boolean {
        return if (StringUtils.isEmpty(sign)) {
            false
        } else sign == createSign(map)
    }



    /**
     * 判断签名是否相等(默认secret的情况)
     *
     * @param sign
     * @param map
     * @param param 对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
     * @return
     */
    fun checkSign(sign: String, map: Map<Any, Any>, vararg param: String): Boolean {
        return checkSign(sign, map2SortedMap(map, *param))
    }

    /**
     * 生成签名(map中所有字段都会参与生成签名)
     *
     *
     * 注意，如果map中的value为null，目前是忽略操作
     *
     * @param map
     * @return
     */
    fun createSign(map: Map<Any, Any>): String {
        return createSign(DEFAULT_SECRET, map)
    }

    /**
     * 生成签名(map中部分字段生成签名)
     *
     *
     * 注意，如果map中的value为null，目前是忽略操作
     *
     * @param map
     * @param param 对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
     * @return
     */
    fun createSign(map: Map<Any, Any>, vararg param: String): String {
        return createSign(DEFAULT_SECRET, map, *param)
    }

    /**
     * 生成签名
     * 注意，如果map中的value为null，目前是忽略操作
     *
     * @param keySecret 自定义密钥
     * @param map       需要生成签名的
     * @return
     */
    fun createSign(keySecret: String, map: Map<Any, Any>): String {
        return createSign(keySecret, map2SortedMap(map))
    }

    /**
     * 生成签名
     * 注意，如果map中的value为null，目前是忽略操作
     *
     * @param keySecret 自定义密钥
     * @param map       需要生成签名的map
     * @param param     对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
     * @return
     */
    fun createSign(keySecret: String, map: Map<Any, Any>, vararg param: String): String {
        return createSign(keySecret, map2SortedMap(map, *param))
    }

    /**
     * 签名算法sign（微信支付使用的方式）
     * 注意，如果map中的value为null，目前是忽略操作
     *
     * @param keySecret
     * @param parameters 因为需要排序，所以使用
     * @return 返回签名，异常返回null
     */
    fun createSign(keySecret: String, parameters: SortedMap<Any, Any>): String {
        return try {
            val sb = StringBuilder()
            //所有参与传参的参数按照accsii排序（升序）
            val es: Set<*> = parameters.entries
            for (e in es) {
                val entry = e as Map.Entry<*, *>
                val k = entry.key as String
                val v = entry.value
                if (null != v && "" != v && "sign" != k && "key" != k) {
                    sb.append(k).append("=").append(v).append("&")
                }
            }
            sb.append("key=").append(keySecret)
            EncryptUtils.encryptMD5ToString(sb.toString()).toUpperCase(Locale.ROOT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * map转SortedMap
     *
     * @param map
     * @return
     */
    private fun map2SortedMap(map: Map<Any, Any>): SortedMap<Any, Any> {
        val parameters: SortedMap<Any, Any> = TreeMap()
        for (o in map.entries) {
            val key = o.key
            val `val` = o.value
            parameters[key] = `val`
        }
        return parameters
    }

    /**
     * map转SortedMap
     *
     * @param map
     * @param param 对于某些场景,map中不是所有字段都需要校验签名的情况,会根据传入的param当做要校验的字段
     * @return
     */
    private fun map2SortedMap(map: Map<Any, Any>, vararg param: String): SortedMap<Any, Any> {
        val parameters: SortedMap<Any, Any> = TreeMap()
        for (key in param) {
            parameters[key] = map[key]
        }
        return parameters
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(">>>模拟微信支付<<<")
        println("==========华丽的分隔符==========")
        //微信api提供的参数
        val appid = "wxd930ea5d5a258f4f"
        val mch_id = "10000100"
        val device_info = "1000"
        val body = "test"
        val nonce_str = "ibuaiVcKdpRxkhJA"
        val parameters: MutableMap<Any, Any> = HashMap()
        // null会被忽略的
        parameters["id"] = ""
        parameters["appid"] = appid
        parameters["mch_id"] = mch_id
        parameters["device_info"] = device_info
        parameters["body"] = body
        parameters["nonce_str"] = nonce_str

        //        String characterEncoding = "UTF-8";
        val Key = "192006250b4c09247ec02edce69f6a2d"
        val weixinApiSign = "9A0A8659F005D6984697E2CA0A9CF3B7"
        println("微信的签名是：$weixinApiSign")
        val mySign = createSign(Key, parameters)
        println("我     的签名是：$mySign")
        if (weixinApiSign == mySign) {
            println("恭喜你成功了~")
        } else {
            println("失败~")
        }
    }
}