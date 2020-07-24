/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.akingyin.media.R

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/24 11:54
 * @version V1.0
 */
class CameraSettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        arguments?.let {
            if(!it.getString("sharedPreferencesName").isNullOrEmpty()){
                preferenceManager.sharedPreferencesName=it.getString("sharedPreferencesName")
            }
        }
        setPreferencesFromResource(R.xml.camera_preferences_fragment, rootKey)
    }









    companion object{
        fun   newInstance(sharedPreferencesName:String):CameraSettingFragment{
            return CameraSettingFragment().apply {
                arguments = Bundle().apply {
                    putString("sharedPreferencesName",sharedPreferencesName)
                }
            }
        }
    }

}