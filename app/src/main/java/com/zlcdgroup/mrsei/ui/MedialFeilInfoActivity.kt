/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.startActivityForResult
import com.akingyin.base.utils.EasyActivityResult
import com.akingyin.bmap.BDLocationService
import com.akingyin.bmap.BDMapManager
import com.akingyin.bmap.PanoramaBaiduMapActivity
import com.akingyin.bmap.SelectLocationBaiduActivity
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.glide.GlideEngine
import com.akingyin.media.ui.fragment.MedialFileInfoFragment
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.zlcdgroup.mrsei.R

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/7 16:12
 * @version V1.0
 */
class MedialFeilInfoActivity:SimpleActivity() {

    override fun initInjection() {

    }

    override fun getLayoutId()= R.layout.activity_test

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
       val filePath =  intent.getStringExtra("filePath")?:""

        val fragmnet = MedialFileInfoFragment.newInstance(filePath,true,true)
        fragmnet.imageEngine = GlideEngine.getGlideEngineInstance()
        fragmnet.locationEngine = object :LocationEngine{
            override fun getNewLocation(locType: String, locLat: Double?, locLng: Double?, call: (lat: Double, lng: Double, addr: String) -> Unit) {
                if (null == locLat || locLat <= 0) {
                    val bdLocationService = BDLocationService.getLocationServer(this@MedialFeilInfoActivity)
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
               return  BDMapManager.getBdMapGeocoderAddr(lat,lng,locType)
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.container,fragmnet,"medialinfo")
                .commit()
    }

    override fun startRequest() {

    }
}