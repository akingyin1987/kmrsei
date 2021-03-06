package com.akingyin.bmap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import com.akingyin.base.ext.click
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.bmap.vo.PickupLatLngVo
import com.akingyin.map.R
import com.akingyin.map.databinding.ActivityBdmapCoordinatePickupBinding
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.zlcdgroup.nfcsdk.RfidInterface

import java.text.MessageFormat
import kotlin.math.abs

/**
 * 百度地图坐标拾取
 * @ Description:
 * @author king
 * @ Date 2020/5/21 17:36
 * @version V1.0
 */
class CoordinatePickupBaiduMapActivity : BaseBDMapActivity(){
    companion object{
        const val   PICKUP_DATA_KEY="pickup_data_key"
        const val   PICKUP_DATA_LAT="lat"
        const val   PICKUP_DATA_LNG="lng"
        const val   PICKUP_DATA_ADDR="addr"
    }

    lateinit var  pickupLatLng: PickupLatLngVo
    lateinit var  viewBinding : ActivityBdmapCoordinatePickupBinding

    override fun getLocationBitmap(): BitmapDescriptor? {
       return null
    }

    override fun autoLocation()=false

    override fun handTag(rfid: String?, rfidInterface: RfidInterface?) {

    }

    override fun initInjection() {

    }

    override fun getLayoutId()= R.layout.activity_bdmap_coordinate_pickup

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun startRequest() {
    }

    private   var    locationNum = 0
    private   var    stepLen = 30
    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        viewBinding = ActivityBdmapCoordinatePickupBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun initView() {
        super.initView()
         pickupLatLng = intent.getSerializableExtra(PICKUP_DATA_KEY)?.let {
              it as PickupLatLngVo
         }?:PickupLatLngVo()

        println("---pickupLatLng---->>$pickupLatLng")
         if(pickupLatLng.onlysee && pickupLatLng.currentLng<=0){
             showError("当前定位信息不全，无法查看！")
             finish()
             return
         }
         pickupLatLng.oldCurrentLat = pickupLatLng.currentLat
         pickupLatLng.oldCurrentLng = pickupLatLng.oldCurrentLng
        val totalBar  = findViewById<Toolbar>(R.id.toolbar)

        setToolBar(totalBar,"百度坐标拾取")
        viewBinding.rlDir.visibility = if(pickupLatLng.onlysee){ View.GONE}else{View.VISIBLE}
        viewBinding.latlngStep.visibility=if(pickupLatLng.onlysee){ View.GONE}else{View.VISIBLE}
        bdMapManager.setMapStatusChange (onChangeLocation = {
            if(stepLen == -1){
                it?.let {
                    mapStatus ->
                    onCreateMoveMarker(mapStatus.target.latitude,mapStatus.target.longitude)
                   // bdMapManager.setMapCenter(pickupLatLng.currentLat,pickupLatLng.currentLng,bdMapManager.getCurrentZoomLevel())
                }
            }
        },onChangeFinish = {
            if(stepLen == -1){
                viewBinding.latlngStep.visiable()
                viewBinding.rlDir.visiable()
            }

        },onChangeStart = {
            if(stepLen == -1){
                viewBinding.latlngStep.gone()
                viewBinding.rlDir.gone()
            }

        })
        viewBinding.rbStepMap.click {

            if(pickupLatLng.currentLat<=0 || pickupLatLng.currentLng<=0){
                viewBinding.rbStepMap.isChecked = false
                stepLen = 0
                showError("当前没有定位信息，无法通过拖动移动坐标！")
                return@click
            }
            bdMapManager.setMapCenter(pickupLatLng.currentLat,pickupLatLng.currentLng,bdMapManager.getCurrentZoomLevel())

        }
        viewBinding.rgStep.setOnCheckedChangeListener { _, checkedId ->

           stepLen =  when(checkedId){
                R.id.rb_step_one-> 1
                R.id.rb_step_tow -> 5
                R.id.rb_step_three->10
                R.id.rb_step_fore->30
                R.id.rb_step_five->100
                R.id.rb_step_map->{

                    -1
                }
                else ->{
                    0
                }
            }
        }
        location_icon.click {
            location_switcher.showNext()
            bdMapManager.startLoction()
        }
        bdMapManager.baiduMap.setOnMarkerDragListener(object :BaiduMap.OnMarkerDragListener{
            override fun onMarkerDragEnd(p0: Marker?) {
                p0?.let {
                    onCreateMoveMarker(it.position.latitude,it.position.longitude)
                }
            }

            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {
                p0?.let {
                    viewBinding.tvLalnginfo.text =MessageFormat.format("当前坐标: {0,number,#.######}/{1,number,#.######}",
                            it.position.latitude,it.position.longitude)
                }


            }
        })
        findViewById<ImageButton>(R.id.ib_right).click {
             if(pickupLatLng.currentLat <=0 || pickupLatLng.currentLng<=0){
                 showError("当前没有定位信息！")
                 return@click
             }
             onCreateMoveMarker(pickupLatLng.currentLat,pickupLatLng.currentLng+stepLen/1E6)
        }
        findViewById<ImageButton>(R.id.ib_left).click {
            if(pickupLatLng.currentLat <=0 || pickupLatLng.currentLng<=0){
                showError("当前没有定位信息！")
                return@click
            }
            onCreateMoveMarker(pickupLatLng.currentLat,pickupLatLng.currentLng-stepLen/1E6)
        }
        findViewById<ImageButton>(R.id.ib_down).click {
            if(pickupLatLng.currentLat <=0 || pickupLatLng.currentLng<=0){
                showError("当前没有定位信息！")
                return@click
            }
            onCreateMoveMarker(pickupLatLng.currentLat-stepLen/1E6,pickupLatLng.currentLng)
        }

        findViewById<ImageButton>(R.id.ib_up).click {
            if(pickupLatLng.currentLat <=0 || pickupLatLng.currentLng<=0){
                showError("当前没有定位信息！")
                return@click
            }
            onCreateMoveMarker(pickupLatLng.currentLat+stepLen/1E6,pickupLatLng.currentLng)
        }

        findViewById<ImageButton>(R.id.ib_center).click {
            if(pickupLatLng.currentLat <=0 || pickupLatLng.currentLng<=0){
                showError("当前没有定位信息！")
                return@click
            }
            onCreateMoveMarker(pickupLatLng.currentLat,pickupLatLng.currentLng)
            bdMapManager.setMapCenter(pickupLatLng.currentLat,pickupLatLng.currentLng,bdMapManager.getCurrentZoomLevel())
        }
        location_progress?.click {
            location_switcher.showNext()
            bdMapManager.stopLoction()
            locationNum=0
        }
    }

    private var bitmap: BitmapDescriptor = BitmapDescriptorFactory
            .fromResource(R.drawable.map_pin)
    private   var  marker: Marker? = null
    override fun onMapLoadComplete() {
        super.onMapLoadComplete()
        if(pickupLatLng.currentLat>0 && pickupLatLng.currentLng>0){
            bdMapManager.setMapCenter(pickupLatLng.currentLat,pickupLatLng.currentLng)
            onCreateMoveMarker(pickupLatLng.currentLat,pickupLatLng.currentLng,pickupLatLng.locationAddr)
            println("添加marker-->")

        }else{
            bdMapManager.startLoction()
        }

    }

    override fun changeMyLocation(bdLocation: BDLocation) {
        super.changeMyLocation(bdLocation)
        if(bdLocation.locType == BDLocation.TypeGpsLocation){
            bdMapManager.stopLoction()
            bdMapManager.setMapCenter(bdLocation.latitude,bdLocation.longitude)
            location_switcher.showNext()
            locationNum = 0
            onCreateMoveMarker(bdLocation.latitude,bdLocation.longitude,bdLocation.addrStr)
            return
        }
        if(locationNum>=5){
            bdMapManager.stopLoction()
            bdMapManager.setMapCenter(bdLocation.latitude,bdLocation.longitude)
            location_switcher.showNext()
            locationNum = 0
            onCreateMoveMarker(bdLocation.latitude,bdLocation.longitude,bdLocation.addrStr)
        }
        locationNum++
    }

   private fun   onCreateMoveMarker(lat:Double,lng:Double,addr:String=""){
        pickupLatLng.currentLat = lat
        pickupLatLng.currentLng = lng
        pickupLatLng.locationAddr = addr
        viewBinding.tvLalnginfo.post {
            viewBinding.tvLalnginfo.text =MessageFormat.format("当前坐标: {0,number,#.######}/{1,number,#.######}",lat,lng)
        }

        marker=if(null == marker){
            bdMapManager.addSingleMarker(MarkerOptions().apply {
                position(LatLng(lat,lng))
                icon(bitmap)
                draggable(pickupLatLng.draggable)
            })
        }else{
            marker?.position = LatLng(lat,lng)
            marker
        }

    }

    override fun onFristMyLocation(bdLocation: BDLocation) {
        super.onFristMyLocation(bdLocation)
        if(pickupLatLng.currentLat<=0){
            onCreateMoveMarker(bdLocation.latitude,bdLocation.longitude,bdLocation.addrStr)
            bdMapManager.setMapCenter(bdLocation.latitude,bdLocation.longitude,bdMapManager.getMapMaxZoomLevel()-1)
            bdMapManager.stopLoction()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map_save,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_map_item_save){
            println("onOptionsItemSelected-->")
           if(abs(pickupLatLng.currentLat - pickupLatLng.oldCurrentLat) >0.000001
                   || abs(pickupLatLng.currentLng-pickupLatLng.oldCurrentLng)>0.000001){
               println("data=>${pickupLatLng.toString()}")
               setResult(Activity.RESULT_OK, Intent().apply {
                   putExtra(PICKUP_DATA_LAT,pickupLatLng.currentLat)
                   putExtra(PICKUP_DATA_LNG,pickupLatLng.currentLng)
                   putExtra(PICKUP_DATA_ADDR,pickupLatLng.locationAddr)
               })
               finish()
           }else{
               showError("坐标未发生改变")
           }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap.recycle()
    }
}