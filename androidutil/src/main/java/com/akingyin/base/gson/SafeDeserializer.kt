package com.akingyin.base.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * @ Description:
 * @author king
 * @ Date 2020/12/15 12:40
 * @version V1.0
 */
class SafeDeserializer<T>(private val gson: Gson) : JsonDeserializer<T> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T {
       return try {
            val jsonObject = json?.asJsonObject?: JsonObject()
            removeNullsFromJson(jsonObject)
             gson.fromJson(jsonObject,typeOfT)
        }catch (e:Exception){
            e.printStackTrace()
            gson.fromJson(JsonObject(),typeOfT)
        }

    }

    private fun removeNullsFromJson(jsonObject: JsonObject) {
        val iterator = jsonObject.keySet().iterator()

        while (iterator.hasNext()) {
            val key = iterator.next()
            when(val json = jsonObject[key]){
                is JsonObject -> removeNullsFromJson(json)
                is JsonArray -> {

                    json.asJsonArray.forEach {
                        if(it.isJsonObject){
                            removeNullsFromJson(it.asJsonObject)
                        }

                    }
                }
                is JsonNull -> iterator.remove()
            }
        }
    }
}