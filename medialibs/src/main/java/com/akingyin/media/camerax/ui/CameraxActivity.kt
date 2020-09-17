/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

@file:Suppress("DEPRECATION")

package com.akingyin.media.camerax.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavArgument

import androidx.navigation.fragment.NavHostFragment
import com.akingyin.base.SimpleActivity
import com.akingyin.base.config.AppFileConfig
import com.akingyin.base.utils.StringUtils
import com.akingyin.media.R
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.databinding.ActivityCameraxNavBinding

private const val IMMERSIVE_FLAG_TIMEOUT = 500L
const val FLAGS_FULLSCREEN =
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
/**
 * @ Description:
 * @author king
 * @ Date 2020/9/16 12:22
 * @version V1.0
 */

class CameraxActivity : SimpleActivity() {
    lateinit var  cameraxNavBinding: ActivityCameraxNavBinding
    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_camerax_nav
    override fun useViewBind() = true
    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun initViewBind() {
        cameraxNavBinding = ActivityCameraxNavBinding.inflate(layoutInflater)
        setContentView(cameraxNavBinding.root)
        val  fragment = supportFragmentManager.findFragmentById(R.id.nav_camerax_graph)
        println("null==${null == fragment}")
        supportFragmentManager.fragments.firstOrNull()?.let {
            if(it is NavHostFragment){
                it.navController.graph.addArgument("filePath",NavArgument.Builder().setDefaultValue(Bundle().apply {
                    putString("fileDir", AppFileConfig.APP_FILE_ROOT)
                    putString("fileName", StringUtils.getUUID()+".jpg")
                }).build())
            }
        }
      //  Navigation.findNavController(this,R.id.nav_camerax_graph)
//        Navigation.findNavController(cameraxNavBinding.fragmentContainer).graph.addArgument("filePath",
//                NavArgument.Builder()
//                        .setDefaultValue(Bundle().apply {
//                            putString("fileDir",AppFileConfig.APP_FILE_ROOT)
//                            putString("fileName",StringUtils.getUUID()+".jpg")
//                        }).build())

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {

    }

    override fun startRequest() {

    }

    override fun onResume() {
        super.onResume()
        cameraxNavBinding.fragmentContainer.postDelayed({
            cameraxNavBinding.fragmentContainer.systemUiVisibility = FLAGS_FULLSCREEN
        },IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_CAMERA) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent().apply {
                action = CameraManager.KEYDOWN_VOLUME_KEY_ACTION
                putExtra("keyCode", keyCode)

            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}