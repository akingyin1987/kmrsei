package com.zlcdgroup.mrsei.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.akingyin.map.TestUtil
import com.akingyin.map.base.AbstractAMapMarkersActivity
import com.akingyin.map.model.IMarkerModel
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.TestMarkerItem

/**
 * @ Description:
 * @author king
 * @ Date 2020/1/6 14:00
 * @version V1.0
 */
class TestAmapMarkerActivity : AbstractAMapMarkersActivity() {


    override fun showPathPlan() = true



    override fun getTitleInfo()="Amap测试"

    override fun onLoadMarkerDatas(): MutableList<IMarkerModel> {
        return  mutableListOf<IMarkerModel>().apply {
            for (i in 1..100){
                var latlng = TestUtil.Latlng()
                add(TestMarkerItem(latlng[0],latlng[1],"index=$i"))
            }
        }

    }

    override fun filterMakerToPath(iMarkerModels: MutableList<IMarkerModel>) {
    }

    override fun onCreateView(inflater: LayoutInflater)=inflater.inflate(R.layout.activity_amap_test,null)


    override fun getMarkerBitmapDescriptor(iMarkerModel: IMarkerModel) : BitmapDescriptor {

        return  BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)
    }

    override fun getMinDisFlushPath()=50

    override fun initView(left: TextView?, center: TextView?, right: TextView?, postion: Int, iMarkerModel: IMarkerModel?, vararg views: View?) {
    }

    override fun onOperation(postion: Int, iMarkerModel: IMarkerModel) {
    }

    override fun onTuWen(postion: Int, iMarkerModel: IMarkerModel) {
    }

    override fun onObjectImg(postion: Int, iMarkerModel: IMarkerModel, view: View?) {
    }

    override fun displayInOrder()=true
    override fun loadImageView(path: String?, context: Context?, imageView: ImageView) {
        imageView.setImageResource(R.drawable.error_img)
    }

    override fun filterAddMakerToMap(iMarkerModels: MutableList<IMarkerModel>) {
    }
}