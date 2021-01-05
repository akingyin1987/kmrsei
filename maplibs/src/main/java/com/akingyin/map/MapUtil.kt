package com.akingyin.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.dialog.ToastUtil

import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan
import com.baidu.mapapi.utils.route.RouteParaOption


/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/3/2 17:30
 */
object MapUtil {

    @JvmStatic
    fun getLatlngAverage(latLngs: Collection<LatLng>): LatLng {
        var lat = 0.0
        var lng = 0.0
        for (latLng in latLngs) {
            if (lat == 0.0 || lng == 0.0) {
                lat = latLng.latitude
                lng = latLng.longitude
            } else {
                lat = (lat + latLng.latitude) / 2.0
                lng = (lng + latLng.longitude) / 2.0
            }
        }
        return LatLng(lat, lng)
    }

    @JvmStatic
    fun getLatlngCentent(latLngs: Collection<LatLng>): LatLng {
        var minlat = 0.0
        var maxlat = 0.0
        var minlng = 0.0
        var maxlng = 0.0
        for (latLng in latLngs) {
            if (minlat == 0.0 || minlng == 0.0) {
                minlat = latLng.latitude
                minlng = latLng.longitude
                maxlat = latLng.latitude
                maxlng = latLng.longitude
            } else {
                if (minlat > latLng.latitude) {
                    minlat = latLng.latitude
                }
                if (minlng > latLng.longitude) {
                    minlng = latLng.longitude
                }
                if (maxlat < latLng.latitude) {
                    maxlat = latLng.latitude
                }
                if (maxlng < latLng.longitude) {
                    maxlng = latLng.longitude
                }
            }
        }
        return LatLng((minlat + maxlat) / 2.0, (minlng + maxlng) / 2.0)
    }

    fun initMap(context: Context) {
        SDKInitializer.initialize(context)
    }

    /**
     * 提供百度地图路径规划选择
     * @param context Context
     * @param latLng LatLng
     */
    fun  showMapRoutePlanByBdLocation(context: Context, startLatLng: LatLng, startName: String = "起点", endLatLng: LatLng, endName: String = "终点"){

        MaterialDialogUtil.showSingleSelectItemDialog(context, "请选择路径规划", datas = listOf("百度地图驾车路线", "百度地图公交路线", "百度地图步行路线",
                "高德地图驾车路线","高德地图公交路线","高德地图步行路线")){ _, selectIndex ->
          try {
              when(selectIndex){
                  0 -> {
                      BaiduMapRoutePlan.openBaiduMapDrivingRoute(RouteParaOption()
                              .endName(endName)
                              .startName(startName)
                              .startPoint(startLatLng)
                              .endPoint(endLatLng), context)
                  }
                  1 -> {
                      BaiduMapRoutePlan.openBaiduMapTransitRoute(RouteParaOption()
                              .busStrategyType(RouteParaOption.EBusStrategyType.bus_time_first)
                              .endName(endName)
                              .startName(startName)
                              .startPoint(startLatLng)
                              .endPoint(endLatLng), context)
                  }

                  2 -> {
                      BaiduMapRoutePlan.openBaiduMapWalkingRoute(RouteParaOption()
                              .endName(endName)
                              .startName(startName)
                              .startPoint(startLatLng)
                              .endPoint(endLatLng), context)
                  }
                  3-> {
                      CoordinateConverter().from(CoordinateConverter.CoordType.BD09LL)
                              .coord(endLatLng).convert().let {
                                  openGaoDeNavi(context,0.0,0.0,null,it.latitude,it.latitude,endName)
                              }

                  }
                  4->{
                      CoordinateConverter().from(CoordinateConverter.CoordType.BD09LL)
                              .coord(endLatLng).convert().let {
                                  openGaoDeNavi(context,0.0,0.0,null,it.latitude,it.longitude,endName,planType = 1)
                              }
                  }
                  5->{
                      CoordinateConverter().from(CoordinateConverter.CoordType.BD09LL)
                              .coord(endLatLng).convert().let {
                                  openGaoDeNavi(context,0.0,0.0,null,it.latitude,it.longitude,endName,planType = 2)
                              }
                  }
              }
          }catch (e: Exception){
              e.printStackTrace()
              if(selectIndex>3){
                  ToastUtil.showError(context, "当前未安装高德地图,无法使用！")
              }else{
                  ToastUtil.showError(context, "出错了,${e.message}")
              }

          }finally {
              if(selectIndex<3){
                  BaiduMapRoutePlan.finish(context)
              }
          }

        }
    }


    /**
     * // 高德地图包名
     */
    const val PN_GAODE_MAP = "com.autonavi.minimap"
    /**
     * 打开高德地图导航功能
     * @param context
     * @param slat 起点纬度
     * @param slon 起点经度
     * @param sname 起点名称 可不填（0,0，null）
     * @param dlat 终点纬度
     * @param dlon 终点经度
     * @param dname 终点名称 必填
     * @param planType 0=架车 1=公交 2=步行
     */
    fun openGaoDeNavi(context: Context, slat: Double, slon: Double, sname: String?, dlat: Double, dlon: Double, dname: String?,sourceApplication:String="",planType:Int=0) {

        println("dlat=$dlat,lng=$dlon")
        val builder = StringBuilder("amapuri://route/plan?sourceApplication=$sourceApplication")
        if (slat != 0.0) {
            builder.append("&sname=").append(sname)
                    .append("&slat=").append(slat)
                    .append("&slon=").append(slon)
        }
        builder.append("&dlat=").append(dlat)
                .append("&dlon=").append(dlon)
                .append("&dname=").append(dname)
                .append("&dev=0")
                .append("&t=$planType")

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage(PN_GAODE_MAP)
        intent.data = Uri.parse(builder.toString())
        context.startActivity(intent)
    }
}