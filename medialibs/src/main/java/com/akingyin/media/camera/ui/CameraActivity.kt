/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

@file:Suppress("DEPRECATION")

package com.akingyin.media.camera.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.akingyin.base.SimpleActivity
import com.akingyin.base.config.AppFileConfig
import com.akingyin.media.R
import com.akingyin.media.camera.CameraData
import com.akingyin.media.camera.CameraManager
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.databinding.ActivityCameraNavBinding
import com.akingyin.media.engine.LocationManagerEngine
import com.akingyin.media.ui.fragment.MedialFileInfoFragmentDialog



const val IMMERSIVE_FLAG_TIMEOUT = 500L
const val FLAGS_FULLSCREEN =
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/**
 * @ Description:
 * @author king
 * @ Date 2021/2/25 16:27
 * @version V1.0
 */
open class CameraActivity :SimpleActivity(){
    private  var  cameraData = CameraData()


    open  fun  setResultOk(intent: Intent){
        setResult(Activity.RESULT_OK,intent)
        finish()
    }


    private  var  cameraInfoBroadcastReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            intent?.let {
                when(it.action){

                    CameraxManager.KEY_CAMERA_PHOTO_COMPLETE_ACTION->{
                        setResultOk(Intent().apply {
                            putExtra("cameraData",it.getParcelableExtra<CameraData>("cameraData"))
                        })
                    }

                    CameraxManager.KEY_CAMERA_PHOTO_ADD_ACTION->{

                        val complete = it.getBooleanExtra("complete",false)

                        val  filePath = it.getStringExtra("filePath")?:""
                        cameraData.localPath = filePath
                        cameraData.cameraPhotoDatas.add(filePath)
                        when {
                            complete -> {
                                setResultOk(Intent().apply {
                                    putExtra("cameraData",cameraData)
                                })
                            }
                            else -> {}
                        }
                    }

                    CameraxManager.KEY_CAMERA_PHOTO_CANCEL_ACTION->{
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                    CameraxManager.KEY_GET_TAKE_PHOTO_PATH->{
                        val allPath = it.getBooleanExtra("allPath",false)
                        if(allPath){
                            cameraData.cameraPhotoDatas.let {path->

                                CameraxManager.pushTakeAllPhotoPaths(this@CameraActivity,path.toList())
                            }
                        }else{
                            cameraData.cameraPhotoDatas.lastOrNull()?.let {path->

                                CameraxManager.pushTakePhotoPath(this@CameraActivity,path)
                            }
                        }


                    }
                    MedialFileInfoFragmentDialog.KEY_MEDIAL_FILE_INFO_DIALOG_ACTION->{
                        cameraxNavBinding.fragmentContainer.postDelayed({
                            window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
                        }, IMMERSIVE_FLAG_TIMEOUT)
                    }
                    else -> {}
                }
            }
        }
    }
    lateinit var  cameraxNavBinding: ActivityCameraNavBinding


    override fun initInjection() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(cameraInfoBroadcastReceiver, IntentFilter().apply {
            addAction(CameraxManager.KEY_CAMERA_PHOTO_COMPLETE_ACTION)
            addAction(CameraxManager.KEY_CAMERA_PHOTO_CANCEL_ACTION)
            addAction(CameraxManager.KEY_CAMERA_PHOTO_ADD_ACTION)
            addAction(CameraxManager.KEY_GET_TAKE_PHOTO_PATH)
            addAction(MedialFileInfoFragmentDialog.KEY_MEDIAL_FILE_INFO_DIALOG_ACTION)
        })
        setResult(Activity.RESULT_CANCELED)
    }

    override fun getLayoutId() = R.layout.activity_camerax_nav
    override fun useViewBind() = true
    override fun initializationData(savedInstanceState: Bundle?) {

    }

    lateinit var  photoDir:String


    override fun initViewBind() {
        cameraData = CameraData()
        cameraxNavBinding = ActivityCameraNavBinding.inflate(layoutInflater)
        setContentView(cameraxNavBinding.root)
        photoDir = intent.getStringExtra(CameraxManager.KEY_CAMERA_PHOTO_DIR)?: AppFileConfig.APP_FILE_ROOT

        val cameraData = intent.getParcelableExtra("data")?: CameraData()

        val sharedPreferencesName = getSharedPreferencesName()

        cameraxNavBinding.fragmentContainer.post {
            supportFragmentManager.findFragmentById(R.id.fragment_container)?.let {
                val navHostFragment = it  as NavHostFragment
                 navHostFragment.navController.setGraph(R.navigation.nav_camera_graph,PermissionsCameraFragmentArgs.Builder(cameraData,sharedPreferencesName).build().toBundle())
            }
            BaseCameraFragment.setCameraXLocationEngine(getLocationEngine())
        }


    }

    override fun close() {
        super.close()
    }

    open  fun  getLocationEngine(): LocationManagerEngine? = null

    open  fun  getSharedPreferencesName()=intent.getStringExtra("sharedPreferencesName")?:"app_setting"

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {

    }

    override fun startRequest() {

    }

    override fun onResume() {
        super.onResume()
        cameraxNavBinding.fragmentContainer.postDelayed({
            cameraxNavBinding.fragmentContainer.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_CAMERA) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent().apply {
                action = CameraManager.KEYDOWN_VOLUME_KEY_ACTION
                putExtra("keyCode", keyCode)

            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(cameraInfoBroadcastReceiver)
    }
}