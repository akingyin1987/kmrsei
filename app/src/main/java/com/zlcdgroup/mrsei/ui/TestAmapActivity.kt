package com.zlcdgroup.mrsei.ui

import android.location.Location
import android.view.LayoutInflater
import com.akingyin.map.base.BaseAMapActivity
import com.zlcdgroup.mrsei.R

/**
 * @ Description:
 * @author king
 * @ Date 2020/1/6 15:55
 * @version V1.0
 */
class TestAmapActivity : BaseAMapActivity() {

    override fun onCreateView(inflater: LayoutInflater)= inflater.inflate(R.layout.activity_amap_test,null)

    override fun initialization() {
    }

    override fun onLocation(bdLocation: Location?) {

    }
}