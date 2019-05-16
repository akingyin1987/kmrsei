package com.zlcdgroup.mrsei.utils

import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/16 15:12
 * @version V1.0
 */
object  ThemeHelper {

    val LIGHT_MODE = "light"
    val DARK_MODE = "dark"
    val DEFAULT_MODE = "default"

    fun  applyTheme(@NonNull themePref:String){
        when(themePref){
            LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE ->  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> {
                if(BuildCompat.isAtLeastQ()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
                }
            }
        }
    }
}