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
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.startActivity
import com.akingyin.base.ext.startActivityForResult
import com.akingyin.base.utils.EasyActivityResult
import com.akingyin.bmap.BDLocationService
import com.akingyin.bmap.BDMapManager
import com.akingyin.bmap.PanoramaBaiduMapActivity
import com.akingyin.bmap.SelectLocationBaiduActivity
import com.akingyin.media.camera.CameraParameBuild
import com.akingyin.media.camera.ui.BaseCameraFragment

import com.akingyin.media.engine.LocationEngine
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation

import com.zlcdgroup.mrsei.R


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 17:24
 * @version V1.0
 */
class CameraTestActivity : SimpleActivity() {

    override fun initInjection() {


    }

    override fun getLayoutId() = R.layout.activity_camera

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
        val fragment = BaseCameraFragment.newInstance(CameraParameBuild(), sharedPreferencesName = "app_camera_setting")
        fragment.cameraLiveData.observe(this, Observer {
            println("data->$it")
            showSucces("拍照成功->$it")
            startActivity<MedialFeilInfoActivity>(bundle = arrayOf("filePath" to it.localPath))
            finish()
        })
        fragment.locationEngine = object : LocationEngine {

            override fun getNewLocation(locType: String, locLat: Double?, locLng: Double?, call: (lat: Double, lng: Double, addr: String) -> Unit) {
                if (null == locLat || locLat <= 0) {
                    val bdLocationService = BDLocationService.getLocationServer(this@CameraTestActivity)
                    bdLocationService.registerListener(object : BDAbstractLocationListener() {
                        override fun onReceiveLocation(location: BDLocation?) {
                            location?.let {
                                bdLocationService.stop()
                                call(it.latitude, it.longitude, it.addrStr)
                            }


                        }
                    })
                    bdLocationService.start()
                } else {
                    val requestCode = EasyActivityResult.getRandomRequestCode()
                    startActivityForResult<SelectLocationBaiduActivity>(requestCode = requestCode)
                    EasyActivityResult.onActivityResultCall(TAG, requestCode) { _, data ->
                        data?.run {
                            call(getDoubleExtra(PanoramaBaiduMapActivity.LAT_KEY, 0.0), getDoubleExtra(PanoramaBaiduMapActivity.LNG_KEY, 0.0), getStringExtra(PanoramaBaiduMapActivity.ADDR_KEY)
                                ?: "")
                        }
                    }

                }
            }


            override fun getLocationImageUrl(lat: Double, lng: Double, locType: String, localImagePath: String?): String {
                return BDMapManager.getBdMapStaticImageUrl(lat,lng,locType)
            }

            override fun getLocationAddr(lat: Double, lng: Double, locType: String): String {
              return BDMapManager.getBdMapGeocoderAddr(lat,lng,locType)
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.container, fragment, "camera")
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
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                showSucces("收到数据")


            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_CAMERA) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent().apply {
                action = BaseCameraFragment.KEYDOWN_VOLUME_KEY_ACTION
                putExtra("keyCode", keyCode)

            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}