package com.akingyin.base.ext

import com.alibaba.fastjson.JSON

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/28 16:52
 * @version V1.0
 */

inline fun <reified T> T.toJson() = JSON.toJSONString(this)

inline fun <reified T> String.toBean() = JSON.parseObject(this,T::class.java)

inline fun <reified T> String.toListBean() = JSON.parseArray(this,T::class.java)