package com.akingyin.bmap

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.akingyin.map.R
import com.baidu.lbsapi.panoramaview.ImageMarker
import com.baidu.lbsapi.panoramaview.PanoramaView
import com.baidu.lbsapi.panoramaview.PanoramaViewListener
import com.baidu.lbsapi.tools.Point
import kotlinx.android.synthetic.main.activity_baidu_panorama.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * 百度全景
 * @ Description:
 * @author king
 * @ Date 2020/5/20 18:28
 * @version V1.0
 */
class PanoramaBaiduMapActivity : SimpleActivity() {

    companion object {
        const val LAT_KEY = "lat"
        const val LNG_KEY = "lng"
        const val ADDR_KEY = "addr"
    }

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var addr: String = ""
    override fun initInjection() {
        lat = intent.getDoubleExtra(LAT_KEY, lat)
        lng = intent.getDoubleExtra(LNG_KEY, lng)
        addr = intent.getStringExtra(ADDR_KEY)?:""
        }

    override fun getLayoutId()= R.layout.activity_baidu_panorama

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
        panorama.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh)
        panorama.setShowTopoLink(false)
        val imageMarker = ImageMarker().apply {
            setMarkerPosition(Point(lat,lng))
            setMarker(ContextCompat.getDrawable(this@PanoramaBaiduMapActivity,R.drawable.icon_openmap_mark))
        }
        imageMarker.setOnTabMarkListener {
            showTips(addr.isEmpty().yes { "这是目标位置" }.no { addr })
        }
        panorama.addMarker(imageMarker)
        panorama.setPanoramaViewListener(object :PanoramaViewListener{
            override fun onCustomMarkerClick(p0: String?) {

            }

            override fun onLoadPanoramaBegin() {
                println("onLoadPanoramaBegin")
            }

            override fun onLoadPanoramaEnd(p0: String?) {
                println("onLoadPanoramaEnd")
            }

            override fun onMessage(p0: String?, p1: Int) {
                println("onMessage$p0")

            }

            override fun onDescriptionLoadEnd(p0: String?) {

            }

            override fun onLoadPanoramaError(p0: String?) {
                println("onLoadPanoramaError=$p0")
                showError(p0)
            }

            override fun onMoveStart() {

            }

            override fun onMoveEnd() {

            }
        })
        println(Thread.currentThread().name+"-"+Thread.currentThread().id)
        GlobalScope.launch(IO){
            println("thread="+Thread.currentThread().name+"-"+Thread.currentThread().id)

            panorama.setPanorama(lng,lat)
        }
    }

    override fun startRequest() {

    }

    override fun onResume() {
        super.onResume()
        panorama.onResume()
    }

    override fun onPause() {
        super.onPause()
        panorama.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        panorama.destroy()
    }
}





