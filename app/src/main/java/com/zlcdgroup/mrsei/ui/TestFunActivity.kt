/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ble.ui.SearchDeviceListActivity
import com.akingyin.base.config.AppFileConfig
import com.akingyin.media.camerax.ui.CameraxActivity
import com.akingyin.media.model.*
import com.akingyin.media.ui.MediaSelectDownloadViewPager2Activity
import com.akingyin.media.ui.MediaTypeViewpagerActivity
import com.akingyin.media.ui.MediaViewPager2Activity
import com.akingyin.util.MediaIntentUtil
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.model.FunModel
import com.zlcdgroup.mrsei.ui.adapter.FunListAdapter
import kotlinx.android.synthetic.main.activity_test_fun.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @ Description:
 * @author king
 * @ Date 2020/6/23 13:49
 * @version V1.0
 */
class TestFunActivity : SimpleActivity() {

    override fun initInjection() {

    }

    override fun getLayoutId() = R.layout.activity_test_fun

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    lateinit var   funListAdapter: FunListAdapter
    override fun initView() {
       val  toolbar  = findViewById<Toolbar>(R.id.toolbar)
        setToolBar(toolbar,"功能测试")
        recycler.itemAnimator = DefaultItemAnimator()
        funListAdapter = FunListAdapter()
        recycler.adapter = funListAdapter
        GlobalScope.launch (Main){
            funListAdapter.setNewInstance(
                    withContext(IO){
                        mutableListOf<FunModel>().apply {
                            add(FunModel("百度地图marker", Intent(this@TestFunActivity,TestBaiduMapActivity::class.java)))
                            add(FunModel("高德地图marker", Intent(this@TestFunActivity,TestAmapActivity::class.java)))
                            add(FunModel("多媒体查看", Intent(this@TestFunActivity,MediaViewPager2Activity::class.java).apply {
                                putExtra("data",MediaDataListModel().apply {
                                  items = arrayListOf<MediaDataModel>().apply {
                                      add(MediaDataModel.buildModel(localPath = "",serverPath = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1594014042023&di=1eefe5aba70bb14eee01111dff4b8813&imgtype=0&src=http%3A%2F%2Fa0.att.hudong.com%2F64%2F76%2F20300001349415131407760417677.jpg"))
                                      add(MediaDataModel.buildModel(localPath = "",serverPath = "",text = "这是测试",multimediaType = 0))
                                      add(MediaDataModel.buildModel(localPath = "",serverPath = "http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4",
                                      multimediaType = 2))
                                      add(MediaDataModel.buildModel(localPath = "",serverPath = "https://m3.8js.net/20200306/57_yelangDISCOrap.mp3",multimediaType = 3))
                                  }



                                })
                            }))

                            add(FunModel("多媒体查看2", Intent(this@TestFunActivity,MediaSelectDownloadViewPager2Activity::class.java).apply {
                                putExtra("data",MediaDataListModel().apply {
                                    items = arrayListOf<MediaDataModel>().apply {
                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1594014042023&di=1eefe5aba70bb14eee01111dff4b8813&imgtype=0&src=http%3A%2F%2Fa0.att.hudong.com%2F64%2F76%2F20300001349415131407760417677.jpg",downloadPath = AppFileConfig.APP_FILE_ROOT))
                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "",text = "这是测试",multimediaType = 0))
                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4",
                                                multimediaType = 2,downloadPath = AppFileConfig.APP_FILE_ROOT))
                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "https://m3.8js.net/20200306/57_yelangDISCOrap.mp3",multimediaType = 3,downloadPath = AppFileConfig.APP_FILE_ROOT))
                                    }
                                })
                            }))

                            add(FunModel("多媒体查看3",Intent(this@TestFunActivity,MediaTypeViewpagerActivity::class.java)
                                    .apply {
                                        putExtra("data",MediaIncludeMediaDataModel().apply {
                                            val list  =  mutableListOf<MediaDataListTypeModel>()
                                            for (i in 1..5){
                                                list.add( MediaDataListTypeModel().apply {
                                                    text = "这是标题$i"
                                                    items = arrayListOf<MediaDataModel>().apply {
                                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1594014042023&di=1eefe5aba70bb14eee01111dff4b8813&imgtype=0&src=http%3A%2F%2Fa0.att.hudong.com%2F64%2F76%2F20300001349415131407760417677.jpg",downloadPath = AppFileConfig.APP_FILE_ROOT,objectId = 1))
                                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "",text = "这是测试",multimediaType = 0,objectId = 2))
                                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4",
                                                                multimediaType = 2,downloadPath = AppFileConfig.APP_FILE_ROOT,objectId = 3))
                                                        add(MediaDataModel.buildModel(localPath = "",serverPath = "https://m3.8js.net/20200306/57_yelangDISCOrap.mp3",multimediaType = 3,downloadPath = AppFileConfig.APP_FILE_ROOT,objectId = 4))
                                                    }
                                                })
                                            }
                                            items = list

                                        })

                            }))

                            add(FunModel("camera 测试",Intent(this@TestFunActivity,CameraTestActivity::class.java)))
                            add(FunModel("Ble测试",Intent(this@TestFunActivity,SearchDeviceListActivity::class.java)))
                            add(FunModel("camerax 测试", Intent(this@TestFunActivity,CameraxActivity::class.java)))
                            add(FunModel("录音测试",Intent(this@TestFunActivity,TestRecordAudioActivity::class.java)))
                            add(FunModel("stepperView",Intent(this@TestFunActivity,SteperActivity::class.java)))
                            add(FunModel("droPdOWNmENU",Intent(this@TestFunActivity,DropDownMenuActivity::class.java)))
                        }
                    }
            )
        }
        funListAdapter.setOnItemClickListener { _, _, position ->
            if(position == 3){
                println("3333333333333")
                startActivityForResult(funListAdapter.getItem(position).intent,MediaIntentUtil.ACTIVITY_REQUEST_MEDIA_SELECT_DOWNLOAD_CODE)
            }else{
                startActivity(funListAdapter.getItem(position).intent)
            }

        }
    }

    override fun startRequest() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MediaIntentUtil.getMediaSelectAndDownloadItems(requestCode,resultCode,data){
            println("data=$it")
        }
    }
}