/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.ui

import android.graphics.Point
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.camera.CameraSize
import com.akingyin.media.R

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/24 11:54
 * @version V1.0
 */
class CameraSettingFragment : PreferenceFragmentCompat() {
    private  var  cameraSizes:List<Point> = mutableListOf()
    private  var  cameraX = false
    private  var  cameraOld = false
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        arguments?.let {
            if(!it.getString("sharedPreferencesName").isNullOrEmpty()){
                preferenceManager.sharedPreferencesName=it.getString("sharedPreferencesName")
            }
            cameraX = it.getBoolean("cameraX",false)
            cameraOld = it.getBoolean("cameraOld",false)
        }
        setPreferencesFromResource(R.xml.camera_preferences_fragment, rootKey)
        findPreference<Preference>("camera_resolution")?.let {
            val x = preferenceManager.sharedPreferences.getInt(BaseCameraFragment.KEY_CAMERA_RESOLUTION_X,0)
            val y  = preferenceManager.sharedPreferences.getInt(BaseCameraFragment.KEY_CAMERA_RESOLUTION_Y,0)
            if(x ==0 || y == 0){
                it.title = "未知"
            }else{
                it.title = CameraSize(x,y).toString()
            }
            if(cameraOld){
                it.setOnPreferenceClickListener {
                    selectCameraResolution(x,y){
                        width, hight ->
                        it.title = CameraSize(width,hight).toString()
                        preferenceManager.sharedPreferences.edit().run {
                            putInt(BaseCameraFragment.KEY_CAMERA_RESOLUTION_X,width)
                                    .putInt(BaseCameraFragment.KEY_CAMERA_RESOLUTION_Y,hight)
                        }.apply()
                    }
                    return@setOnPreferenceClickListener true
                }
            }else{
                it.isVisible = false
            }

        }

        findPreference<EditTextPreference>("key_camera_auto_takephoto_delaytime")?.let {
            it.summary = "对焦成功后自动拍照延时${preferenceManager.sharedPreferences.getString("key_camera_auto_takephoto_delaytime","")}(秒)"
            it.setOnBindEditTextListener {
                editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        findPreference<EditTextPreference>("key_camera_auto_save_delaytime")?.let {
            it.summary ="拍照成功后自动保存延时${preferenceManager.sharedPreferences.getString("key_camera_auto_save_delaytime","")}(秒)"
            it.setOnBindEditTextListener {
                editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }


    private fun  selectCameraResolution(x:Int, y:Int, callback: (width:Int, hight:Int)->Unit){
        context?.let {
            context ->
           var  selectIndex=0
            cameraSizes.forEachIndexed { index, point ->
               if(point.x * point.y == x * y){
                   selectIndex = index
               }
            }
            MaterialDialogUtil.showSingleSelectItemDialog(context,"选择相机分辨率",selectIndex,cameraSizes.map {
                CameraSize(it.x,it.y)
            }){
                data, _ ->
                callback.invoke(data.width,data.hight)
            }
        }
    }







    companion object{
        fun   newInstance(sharedPreferencesName:String,cameraOld:Boolean = false,cameraX:Boolean = false,cameraSizes:List<Point>):CameraSettingFragment{
            return CameraSettingFragment().apply {
                arguments = Bundle().apply {
                    putString("sharedPreferencesName",sharedPreferencesName)
                    putBoolean("cameraOld",cameraOld)
                    putBoolean("cameraX",cameraX)
                }
            }.also {
              it.cameraSizes = cameraSizes
            }
        }
    }

}