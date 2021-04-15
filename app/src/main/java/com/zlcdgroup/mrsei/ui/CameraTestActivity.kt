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

import android.view.KeyEvent
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.akingyin.base.ext.startRegisterForActivityResult
import com.akingyin.bmap.BDLocationService
import com.akingyin.bmap.BDMapManager
import com.akingyin.bmap.PanoramaBaiduMapActivity
import com.akingyin.bmap.SelectLocationBaiduActivity
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camera.ui.CameraActivity
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.zlcdgroup.mrsei.BuildConfig
import com.zlcdgroup.mrsei.Constants
import java.util.*
import kotlin.properties.Delegates


/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 17:24
 * @version V1.0
 */
@Suppress("DEPRECATION")
class CameraTestActivity : CameraActivity() {

    override fun getLocationEngine(): LocationManagerEngine? {
        return locationManagerEngine
    }

    var  locationEngine:LocationEngine by Delegates.notNull()
    var  locationManagerEngine:LocationManagerEngine?=null
    override fun initView() {

        locationEngine = object : LocationEngine {

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
                    startRegisterForActivityResult<SelectLocationBaiduActivity> {
                        it.data?.run {
                            call(getDoubleExtra(PanoramaBaiduMapActivity.LAT_KEY, 0.0), getDoubleExtra(PanoramaBaiduMapActivity.LNG_KEY, 0.0), getStringExtra(PanoramaBaiduMapActivity.ADDR_KEY)
                                ?: "")
                        }
                    }


                }
            }


            override fun getLocationImageUrl(lat: Double, lng: Double, locType: String, localImagePath: String?): String {
                val code = Constants.apkSign.chunked(2).joinToString(":").toUpperCase(Locale.ROOT)+";"+BuildConfig.APPLICATION_ID
                println("code=$code")
                return BDMapManager.getBdMapStaticImageUrl(lat,lng,locType,code)
            }

            override fun getLocationAddr(lat: Double, lng: Double, locType: String): String {
              return BDMapManager.getBdMapGeocoderAddr(lat,lng,locType)
            }

            override fun cancelLocation() {

            }
        }
        locationManagerEngine = object :LocationManagerEngine{
            override fun createEngine(): LocationEngine {
               return locationEngine
            }
        }

    }

    override fun onStart() {
        super.onStart()
        //全屏显示
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
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
                action = CameraManager.KEYDOWN_VOLUME_KEY_ACTION
                putExtra("keyCode", keyCode)

            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}