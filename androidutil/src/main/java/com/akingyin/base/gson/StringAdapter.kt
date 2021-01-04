package com.akingyin.base.gson



import com.google.gson.*
import java.lang.reflect.Type

class StringAdapter : JsonSerializer<String>, JsonDeserializer<String> {

    override fun serialize(
        src: String?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
      return  src?.let { JsonPrimitive(it) }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {

        println("json->${json},$typeOfT")
       return json?.asString?:""
    }
}