package com.akingyin.base.gson

import com.akingyin.base.utils.DateUtil
import com.google.gson.*
import java.lang.reflect.Type

/**
 * @ Description:
 * @author king
 * @ Date 2020/12/12 16:20
 * @version V1.0
 */
class DateLongStringAdapter : JsonSerializer<Long>,JsonDeserializer<Long> {
    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src?.let { JsonPrimitive(DateUtil.millis2String(it)) }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long? {
        return json?.let {
           try {
               if(it.asString.isNullOrEmpty()){
                   return null
               }
               it.asString.toLongOrNull() ?: DateUtil.string2Millis(it.asString)
           }catch (e:Exception){
               e.printStackTrace()
               null
           }

        }
    }
}