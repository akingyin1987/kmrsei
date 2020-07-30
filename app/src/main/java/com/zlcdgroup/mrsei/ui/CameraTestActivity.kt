/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.app
import com.akingyin.base.ext.startActivityForResult
import com.akingyin.camera.CameraParameBuild
import com.akingyin.camera.ui.BaseCameraFragment
import com.akingyin.camera.ui.CameraSettingActivity
import com.zlcdgroup.mrsei.R
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 17:24
 * @version V1.0
 */
class CameraTestActivity : SimpleActivity() {

    override fun initInjection() {


    }

    override fun getLayoutId()= R.layout.activity_camera

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
        val  fragment = BaseCameraFragment.newInstance(CameraParameBuild(),sharedPreferencesName = "app_camera_setting")
        fragment.cameraLiveData.observe(this, Observer {
            println("data->$it")
            showSucces("拍照成功->$it")
            finish()
        })

        supportFragmentManager.beginTransaction().add(R.id.container,fragment,"camera")
                .commit()

    }

    override fun onStart() {
        super.onStart()
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = option
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun startRequest() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                showSucces("收到数据")
            }
        }
    }
}