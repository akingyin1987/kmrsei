package com.akingyin.bmap

import com.akingyin.base.ext.spGetBoolean
import com.akingyin.base.ext.spGetInt
import com.akingyin.base.ext.spSetBoolean
import com.akingyin.base.ext.spSetInt

/**
 * 地图管理器
 * @ Description:
 * @author king
 * @ Date 2020/5/18 11:02
 * @version V1.0
 */
abstract class IMapManager {




    /**
     * 设置地图中心
     */
  abstract  fun   setMapCenter(lat:Double,lng :Double)


    /**
     * 地图显示类型
     * 1=正常(3D) 2=卫星 3=正常(2D)
     */
    fun    saveMapType(mapType:Int){
       spSetInt("map_show_type",mapType)
    }

    fun   getShowMapType() = spGetInt("map_show_type",1)

    fun   saveMapTraffic(traffic: Boolean){
        spSetBoolean("map_traffic",traffic)
    }

    /**
     * 是否开启地图实时路况
     */
    fun   getShowMapTraffic() = spGetBoolean("map_traffic")


   abstract fun   onResume()

   abstract fun   onPause()

   abstract fun  onDestroy()


}