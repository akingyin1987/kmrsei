package com.akingyin.base.ext

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject



/**
 * @ Description:
 * @author king
 * @ Date 2019/5/28 16:52
 * @version V1.0
 */

inline fun <reified T> T.toJson(): String = JSON.toJSONString(this)

inline fun <reified T> String.toBean(): T = JSON.parseObject(this,T::class.java)

inline fun <reified T> String.toListBean(): MutableList<T> = JSON.parseArray(this,T::class.java)


fun removeNullsFromJson(jsonObject: JSONObject) {
    val iterator = jsonObject.iterator()

    while (iterator.hasNext()) {
        val key = iterator.next().key
        val json = jsonObject[key]
        if(null == json){
            iterator.remove()
        }else{
           when(json){
               is JSONObject -> removeNullsFromJson(json)
               is JSONArray -> {

                   json.forEach {
                       when(it){
                           is JSONObject ->removeNullsFromJson(it)
                       }


                   }
               }
           }
        }



    }
}

fun JSONObject.removeNull(): JSONObject {
    removeNullsFromJson(this)
    return this
}