package com.zlcdgroup.mrsei.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.akingyin.img.ImageLoadUtil
import com.akingyin.map.TestUtil
import com.akingyin.map.base.AbstractClusterMarkerActivity
import com.akingyin.map.model.IMarkerModel
import com.baidu.location.BDLocation
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.TestMarkerItem

/**
 * @ Description:
 * @author king
 * @ Date 2019/12/24 15:57
 * @version V1.0
 */
class TestClusterMarkerActivity : AbstractClusterMarkerActivity<TestMarkerItem>() {

    override fun onLocation(bdLocation: BDLocation?) {
    }

    override fun loadMarkers(): MutableList<TestMarkerItem> {
      return  mutableListOf<TestMarkerItem>().apply {
            for (i in 1..500){
                var latlng = TestUtil.Latlng()
                add(TestMarkerItem(latlng[0],latlng[1],"index=$i"))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater) = inflater.inflate(R.layout.activity_test_map_show_markers,null)

    override fun getLocationBitmap()=null

    override fun onBdNotify(bdLocation: BDLocation?, d: Float) {
    }

    override fun initView(left: TextView?, center: TextView?, right: TextView?, postion: Int, iMarkerModel: IMarkerModel?, vararg views: View?) {
    }

    override fun onOperation(postion: Int, iMarkerModel: IMarkerModel?) {
    }

    override fun onPathPlan(postion: Int, iMarkerModel: IMarkerModel?) {
    }

    override fun onTuWen(postion: Int, iMarkerModel: IMarkerModel?) {
    }

    override fun onObjectImg(postion: Int, iMarkerModel: IMarkerModel?, view: View?) {
    }

    override fun onOtherOperation(postion: Int, iMarkerModel: IMarkerModel?, view: View?) {
    }

    override fun loadImageView(path: String?, context: Context?, imageView: ImageView) {
        ImageLoadUtil.loadImage(this,path,R.drawable.error_img,R.drawable.error_img,imageView)
    }
}