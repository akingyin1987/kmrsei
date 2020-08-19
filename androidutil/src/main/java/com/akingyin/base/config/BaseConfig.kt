/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base.config

import com.akingyin.base.ext.AppTime
import com.akingyin.base.ext.spGetString
import com.akingyin.base.ext.spRemove
import com.akingyin.base.ext.spSetString

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/19 17:07
 * @version V1.0
 */
class BaseConfig private constructor(){

    /** 文件共享权限 */
    private   var   authority : String = ""

    companion object{
        private val Instance: BaseConfig by lazy { BaseConfig() }

        @JvmStatic
        fun getAuthority():String{
            if(Instance.authority.isEmpty()){
                Instance.authority = spGetString("authority")
            }
            return Instance.authority
        }

        @JvmStatic
        fun  saveAuthority(authority:String){
            Instance.authority = authority
            spSetString("authority",authority)
        }

        @JvmStatic
        fun  cleanAuthority(){
            Instance.authority=""
            spRemove("authority")
        }
    }
}