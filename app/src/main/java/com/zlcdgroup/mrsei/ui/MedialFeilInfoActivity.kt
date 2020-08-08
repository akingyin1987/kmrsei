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
import com.akingyin.bmap.BDMapManager
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.glide.GlideEngine
import com.akingyin.media.ui.fragment.MedialFileInfoFragment
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
        fragmnet.imageEngine = GlideEngine()
        fragmnet.locationEngine = object :LocationEngine{
            override fun getNewLocation(locType: String, locLat: Double?, locLng: Double?, call: (lat: Double, lng: Double, addr: String) -> Unit) {

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