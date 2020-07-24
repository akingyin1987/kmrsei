/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.camera.ui

import android.app.Activity
import android.os.Bundle
import com.akingyin.base.SimpleActivity
import com.akingyin.media.R


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/24 12:44
 * @version V1.0
 */
class CameraSettingActivity :SimpleActivity(){

    override fun initInjection() {

    }

    override fun getLayoutId()= R.layout.activity_camera_setting

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }


    override fun initView() {
        setToolBar(findViewById(R.id.toolbar),"相机设置")
        setResult(Activity.RESULT_OK)
        supportFragmentManager.beginTransaction().add(R.id.root_container,CameraSettingFragment.newInstance("app_camera_setting"))
                .commit()
    }



    override fun startRequest() {

    }
}