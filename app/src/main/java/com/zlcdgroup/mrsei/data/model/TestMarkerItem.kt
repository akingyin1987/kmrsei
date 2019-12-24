package com.zlcdgroup.mrsei.data.model

import com.akingyin.map.model.IMarkerModel
import com.baidu.mapapi.clusterutil.clustering.ClusterItem
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.model.LatLng
import com.zlcdgroup.mrsei.R

/**
 * @ Description:
 * @author king
 * @ Date 2019/12/24 15:58
 * @version V1.0
 */
class TestMarkerItem(var   latd : Double,var lngd:Double,var titleStr:String) :IMarkerModel, ClusterItem{





    override fun getPosition() = LatLng(latd,lngd)

    override fun getBitmapDescriptor()= BitmapDescriptorFactory
            .fromResource(R.drawable.icon_openmap_mark)

    override fun getLat() = latd

    override fun getLng() = lngd

    override fun getAppointSort()=1

    override fun setSort(sort: Int) {
    }

    override fun getMarkerDetaiImgPath()="test"

    override fun getBaseInfo()=titleStr

    override fun getTitle()= titleStr

    override fun getData()=null

    override fun getMarkes()=null

    override fun getSortInfo()=""

    override fun setSortInfo(sortInfo: String?) {
    }

    override fun isComplete()=true
}