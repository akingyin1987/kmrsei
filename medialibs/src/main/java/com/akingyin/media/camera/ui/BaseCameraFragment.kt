/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
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
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.hardware.Camera
import android.hardware.Camera.CAMERA_ERROR_SERVER_DIED
import android.hardware.Camera.CAMERA_ERROR_UNKNOWN
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.*
import com.akingyin.base.mvvm.SingleLiveEvent
import com.akingyin.base.utils.HtmlUtils
import com.akingyin.base.utils.PreferencesUtil
import com.akingyin.base.utils.RandomUtil
import com.akingyin.media.camera.*
import com.akingyin.media.MediaConfig
import com.akingyin.media.MediaMimeType
import com.akingyin.media.R
import com.akingyin.media.camera.CameraManager.Companion.KEYDOWN_VOLUME_KEY_ACTION
import com.akingyin.media.camera.CameraManager.Companion.KEY_CAMERA_FLASH
import com.akingyin.media.camera.CameraManager.Companion.KEY_CAMERA_GRID
import com.akingyin.media.camera.CameraManager.Companion.KEY_CAMERA_SHUTTER_SOUND
import com.akingyin.media.camerax.CameraxManager
import com.akingyin.media.databinding.FragmentCameraBinding
import com.akingyin.media.engine.LocationEngine
import com.akingyin.media.engine.LocationManagerEngine
import com.akingyin.media.model.MediaDataListModel
import com.akingyin.media.model.MediaDataModel
import com.akingyin.media.ui.MediaSelectDownloadViewPager2Activity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.CancellationException
import kotlin.properties.Delegates

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/21 15:10
 * @version V1.0
 */

@Suppress("DEPRECATION", "ClickableViewAccessibility")
open class BaseCameraFragment : SimpleFragment() {
    private val args: BaseCameraFragmentArgs by navArgs()
    lateinit var bindView: FragmentCameraBinding
    lateinit var sharedPreferencesName: String
    lateinit var cameraSensorController: CameraSensorController
    var locationEngine: LocationEngine? = null
    var cameraParameBuild: CameraParameBuild by Delegates.notNull()
    var cameraData: CameraData by Delegates.notNull()
    var cameraLiveData: SingleLiveEvent<CameraData> = SingleLiveEvent()

    private var volumeKeyControlBroadcast: BroadcastReceiver = VolumeKeyControlBroadcast()

    private var cameraInfoBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (it.action) {

                    CameraxManager.KEY_CAMERA_PHOTO_ADD_ACTION -> {

                        val complete = it.getBooleanExtra("complete", false)
                        val filePath = it.getStringExtra("filePath") ?: ""
                        println("$TAG,获取到数据通知,complete=$complete,path=$filePath")
                        if (!complete) {
                            cameraData.cameraPhotoDatas.add(filePath)
                            setGalleryThumbnail(filePath)
                        }

                    }

                    else -> {
                    }
                }
            }
        }
    }

    inner class VolumeKeyControlBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == KEYDOWN_VOLUME_KEY_ACTION) {

                    when (it.getIntExtra("keyCode", 0)) {
                        KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            keyVolumeDown()
                        }
                        KeyEvent.KEYCODE_VOLUME_UP -> {
                            keyVolumeUp()
                        }
                        KeyEvent.KEYCODE_CAMERA -> {
                            captureImage()
                        }
                    }
                }
            }
        }
    }


    private var netGrid: Int by Delegates.observable(CameraManager.CameraNetGrid.CAMERA_NET_GRID_NONE) { _, _, newValue ->

        bindView.buttonGrid.setImageResource(when (newValue) {
            CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN -> {
                bindView.groupGridLines.visiable()
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_GRID, true)
                R.drawable.ic_grid_on

            }
            else -> {
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_GRID, false)
                bindView.groupGridLines.gone()
                R.drawable.ic_grid_off
            }
        })

    }


    private var flashMode: Int by Delegates.observable(CameraManager.CameraFlashModel.CAMERA_FLASH_NONE) { _, _, newValue ->
        PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_FLASH, newValue.toString())
        bindView.buttonFlash.setImageResource(when (newValue) {
            CameraManager.CameraFlashModel.CAMERA_FLASH_ON -> {
                R.drawable.ic_flash_on
            }
            CameraManager.CameraFlashModel.CAMERA_FLASH_OFF -> {
                R.drawable.ic_flash_off
            }
            else -> {
                R.drawable.ic_flash_auto
            }
        })
    }


    private var shutterSound: Int by Delegates.observable(CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_NONE) { _, _, newValue ->
        bindView.buttonShutter.setImageResource(when (newValue) {
            CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON -> {
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_SHUTTER_SOUND, true)
                R.drawable.ic_action_volume_on
            }
            else -> {
                PreferencesUtil.put(sharedPreferencesName, KEY_CAMERA_SHUTTER_SOUND, false)
                R.drawable.ic_action_volume_off
            }
        })
    }
    lateinit var activityForResultLauch: ActivityResultLauncher<Intent>
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityForResultLauch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                Timber.tag(TAG).d("返回数据=${activityResult.data}")
                activityResult.data?.getParcelableArrayListExtra<MediaDataModel>("result")?.let { list ->
                    cameraData.cameraPhotoDatas.clear()
                    list.forEach {
                        cameraData.cameraPhotoDatas.add(it.localPath)
                    }
                    setGalleryThumbnail(cameraData.cameraPhotoDatas.lastOrNull())

                }
            }

        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(cameraInfoBroadcastReceiver, IntentFilter().apply {
            addAction(CameraxManager.KEY_CAMERA_PHOTO_ADD_ACTION)
        })
        cameraManager = CameraManager(context) {
            showSucces("运动对焦成功！")

            focusSucessCaptureImage()
        }


    }

    override fun injection() {

    }

    override fun useViewBind() = true


    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View? {
        bindView = FragmentCameraBinding.inflate(inflater, container, false)
        cameraData = args.cameraData
        locationIsEnable.observe(viewLifecycleOwner, {
            locationEngine = if (it) {
                locationEngineManager?.createEngine()
            } else {
                null
            }
            bindView.tvLocation.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }

        })
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                zoomBarJob?.cancel()
                countDownJob?.cancel(cause = CancellationException("返回取消"))
                cameraManager.closeDriver()
                requireActivity().finish()
            }
        })
        return bindView.root
    }

    override fun getLayoutId() = R.layout.fragment_camera

    override fun initEventAndData() {
        sharedPreferencesName = arguments?.getString("sharedPreferencesName", "app_setting")
            ?: "app_setting"
        cameraParameBuild = CameraParameBuild()
        initCameraParame(cameraParameBuild)
        cameraParameBuild.tipContent = cameraData.cameraContentTips
        cameraParameBuild.imageTags = cameraData.imageTag

    }

    open fun initCameraParame(cameraParame: CameraParameBuild = cameraParameBuild, changeCameraParme: Boolean = false) {
        CameraManager.readCameraParame(cameraParame, sharedPreferencesName)
        cameraParame.cameraResolution?.let {
            if (it.x > 0 && it.y > 0) {
                cameraManager.cameraCustomResolution = Point(it.x, it.y)
            }
        }
        netGrid = cameraParame.netGrid
        flashMode = cameraParame.flashModel
        shutterSound = cameraParame.shutterSound
        if (cameraParame.supportLocation) {
            bindView.tvLocation.visiable()
        } else {
            bindView.tvLocation.gone()
        }
        if (changeCameraParme) {
            cameraManager.camera?.let {
                cameraManager.setCameraParametersValues(it, cameraParame) { result, error ->
                    if (!result) {
                        showError(error)
                    }
                }
            }

        }
    }

    lateinit var cameraManager: CameraManager


    override fun initView() {

        cameraSensorController = CameraSensorController(requireContext(), requireActivity().windowManager.defaultDisplay.rotation)
        cameraSensorController.mOrientationChangeListener = object : CameraSensorController.OrientationChangeListener {
            override fun onChange(relativeRotation: Int, uiRotation: Int) {
                var cameraRotation = uiRotation + 90
                if (cameraRotation == 180) {
                    cameraRotation = 0
                }
                if (cameraRotation == 360) {
                    cameraRotation = 180
                }

                if (cameraManager.cameraAngle != cameraRotation) {
                    cameraManager.cameraAngle = cameraRotation

                }
                if (cameraManager.cameraUiAngle != cameraRotation) {
                    cameraManager.cameraUiAngle = cameraRotation
                    CameraManager.startCameraViewRoteAnimator((uiRotation).toFloat(), bindView.buttonShutter, bindView.buttonSetting, bindView.buttonFlash,
                            bindView.buttonGrid, bindView.textCountDown)
                    thumbnail?.let {
                        CameraManager.startCameraViewRoteAnimator((uiRotation).toFloat(), it)
                    }
                    cancelButton?.let {
                        CameraManager.startCameraViewRoteAnimator((uiRotation).toFloat(), it)
                    }


                }
            }
        }

        bindView.tvTip.text = HtmlUtils.getTextHtml(cameraParameBuild.tipContent)
        bindView.rulerView.valueFrom = cameraManager.cameraMinZoom.toFloat()
        bindView.rulerView.valueTo = cameraManager.cameraMaxZoom.toFloat()
        bindView.rulerView.value = cameraManager.cameraCurrentZoom
        if (cameraManager.cameraCurrentZoom > 1) {
            bindView.rulerView.visiable()
        } else {
            bindView.rulerView.gone()
        }
        if (cameraParameBuild.supportLocation) {
            bindView.tvLocation.visiable()
        } else {
            bindView.tvLocation.gone()
        }


        bindView.tvLocation.click {
            getLocationInfo()
        }
//        bindView.fabTakePicture.captureLisenter = object : CaptureButton.onClickTakePicturesListener() {
//            override fun takePictures() {
//                bindView.fabTakePicture.isEnabled = false
//                captureImage()
//            }
//        }
//        bindView.btnCancel.click {
//            countDownJob?.cancel()
//            CameraManager.recoveryCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel,bindView.rlTurn)
//            bindView.fabTakePicture.resetRecordAnim()
//            bindView.fabTakePicture.isEnabled = true
//            bindView.viewFinder.onStartCameraView()
//        }
//        bindView.ivTurnleft.click {
//            rotateTakePhotoBitmap(270)
//        }
//        bindView.ivTurnright.click {
//            rotateTakePhotoBitmap(90)
//        }
//        bindView.ivTurncenter.click {
//            rotateTakePhotoBitmap(180)
//        }
        bindView.viewFinder.errorCallback = Camera.ErrorCallback { error, _ ->
            when (error) {
                CAMERA_ERROR_SERVER_DIED -> {
                    showError("相机服务异常，请退出或重启手机,错误代码${error}")
                }
                CAMERA_ERROR_UNKNOWN -> {
                    showError("相机出错了,错误代码${error}")
                }
            }
        }
//        bindView.btnConfig.click {
//
//            countDownJob?.cancel()
//            cameraLiveData.postValue(CameraData().apply {
//                mediaType = MediaConfig.TYPE_IMAGE
//                localPath = bindView.viewFinder.cameraParameBuild.localPath
//            })
//        }
        bindView.buttonGrid.click {
            toggleGrid()
        }
        bindView.buttonShutter.click {
            toggleShutter()
        }
        bindView.buttonFlash.click {
            bindView.layoutFlashOptions.circularReveal(it)
        }
        bindView.buttonFlashAuto.click {
            closeFlashAndSelect(CameraManager.CameraFlashModel.CAMERA_FLASH_AUTO)
        }
        bindView.buttonFlashOn.click {
            closeFlashAndSelect(CameraManager.CameraFlashModel.CAMERA_FLASH_ON)
        }
        bindView.buttonFlashOff.click {
            closeFlashAndSelect(CameraManager.CameraFlashModel.CAMERA_FLASH_OFF)
        }
        bindView.buttonSetting.click {
            startCameraSettingActivity()
        }
        bindView.textCountDown.click {
            countDownJob?.cancel()
            showSucces("倒计时已取消")
        }
        bindView.rulerView.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {

                showCameraZoomBar(value)
            }
        }


    }

    var thumbnail: ImageButton? = null
    var cancelButton: ImageButton? = null
    private fun updateCameraUi() {

        bindView.root.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            bindView.root.removeView(it)
        }
        // Inflate a new view containing all UI for controlling the camera
        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, bindView.root)
        controls.findViewById<ImageButton>(R.id.camera_capture_button).click {
            captureImage()
        }
        thumbnail = controls.findViewById(R.id.photo_view_button)
        CameraxManager.getTakePhotoPath(requireContext())
        controls.findViewById<ImageButton>(R.id.photo_view_button).click {
            MediaDataListModel().apply {
                items = cameraData.cameraPhotoDatas.map {
                    MediaDataModel().apply {
                        localPath = it
                        multimediaType = MediaMimeType.ofImage()
                        checked = true
                    }
                }

            }.let {data->
                activityForResultLauch.launch(Intent(requireContext(), MediaSelectDownloadViewPager2Activity::class.java).apply {
                    putExtra("data",data)
                })
            }


        }
        controls?.findViewById<ImageButton>(R.id.camera_switch_button)?.click {
            CameraxManager.sendTakePhtotCancel(requireContext())
        }
        cancelButton = controls?.findViewById(R.id.camera_switch_button)
        if (cameraData.supportMultiplePhoto) {
            cancelButton?.visiable()
            thumbnail?.visiable()
        } else {
            cancelButton?.gone()
            thumbnail?.gone()
        }

    }

    private fun getLocationInfo() {
        locationEngine?.let {
            it.getNewLocation(CameraManager.LocalType.CORR_TYPE_BD09, cameraParameBuild.lat, cameraParameBuild.lng) { lat, lng, addr ->
                cameraParameBuild.locType = CameraManager.LocalType.CORR_TYPE_BD09
                cameraParameBuild.lat = lat
                cameraParameBuild.lng = lng
                bindView.tvLocation.text = addr
            }
        } ?: showError("暂不支持定位信息")
    }

    /**
     * 对焦成功，自动拍照
     */
    private fun focusSucessCaptureImage() {
        if (cameraParameBuild.supportFocesedAutoPhoto) {
            countDownStop()
            if (cameraParameBuild.focesedAutoPhotoDelayTime > 0) {

                countDownStart(cameraParameBuild.focesedAutoPhotoDelayTime) {
                    captureImage()
                }
            } else {
                captureImage()
            }
        }

    }

    /** 拍照*/
    private fun captureImage() {

        cameraParameBuild.cameraAngle = cameraManager.cameraAngle
        if (cameraData.supportMultiplePhoto) {
            cameraData.localPath = cameraData.dirRootPath + File.separator + RandomUtil.randomUUID + ".jpg"
        }

        cameraParameBuild.localPath = cameraData.localPath
        bindView.viewFinder.takePhoto(cameraParameBuild) { result, error ->
            if (result) {
                // CameraManager.startTypeCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel,bindView.rlTurn)
                captureImageSuccess()
            } else {
                countDownJob?.cancel()
//                CameraManager.recoveryCaptureAnimator(bindView.fabTakePicture, bindView.btnConfig, bindView.btnCancel,bindView.rlTurn)
//                bindView.fabTakePicture.resetRecordAnim()
//                bindView.fabTakePicture.isEnabled = true
                // bindView.viewFinder.onStartCameraView()
                showError(error)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view?.postDelayed({
                view?.foreground = ColorDrawable(Color.WHITE)
                view?.postDelayed({
                    view?.foreground = null
                }, ANIMATION_FAST_MILLIS)
            }, ANIMATION_SLOW_MILLIS)
        }
    }


    open fun captureImageSuccess() {
        if (cameraData.supportMultiplePhoto) {
            if (cameraParameBuild.autoSavePhotoDelayTime == 0) {
                setGalleryThumbnail(cameraParameBuild.localPath)
                cameraData.cameraPhotoDatas.add(cameraParameBuild.localPath)
                cameraLiveData.value = CameraData().apply {
                    if (cameraData.supportMultiplePhoto) {
                        supportMultiplePhoto = true
                        cameraPhotoDatas.add(cameraParameBuild.localPath)
                    } else {
                        supportMultiplePhoto = false
                        mediaType = MediaConfig.TYPE_IMAGE
                        localPath = cameraParameBuild.localPath
                    }
                }
                return
            }
        }
        cameraManager.stopPreview()
        cameraManager.closeDriver()
        bindCameraInit = false
        findNavController().navigate(BaseCameraFragmentDirections.actionCameraToPhoto(cameraParameBuild.localPath, sharedPreferencesName, cameraData))
    }


    private fun setGalleryThumbnail(uri: String?) {
        if (cameraData.supportMultiplePhoto) {
            println("thumbnail=${null == thumbnail}")
            thumbnail?.post {
                thumbnail?.let {

                    it.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())
                    Glide.with(it)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(it)
                }

            }
        }

    }


    private fun closeFlashAndSelect(@CameraManager.CameraFlashModel flash: Int) = bindView.layoutFlashOptions.circularClose(bindView.buttonFlash) {
        flashMode = flash
        cameraManager.camera?.let {
            cameraManager.setCameraFlashModel(it, flashMode) { result, error ->
                if (!result) {
                    showError("出错了,${error}")
                }
            }
        }

    }

    private lateinit var startActivitylaunch: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivitylaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                initCameraParame(changeCameraParme = true)
            }
        }
    }

    open fun startCameraSettingActivity() {
        startActivitylaunch.launch(Intent(requireContext(), CameraSettingActivity::class.java).apply {
            cameraManager.camera?.let {
                putExtra("cameraSizes", CameraManager.findSuportCameraSizeValue(it.parameters))
            }
            putExtra("sharedPreferencesName", sharedPreferencesName)
            putExtra("cameraOld", true)
            putExtra("cameraX", false)
        })

    }

    private fun toggleShutter() {
        bindView.buttonShutter.toggleButton(shutterSound == CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON,
                180F,
                R.drawable.ic_action_volume_off,
                R.drawable.ic_action_volume_on) { flag ->
            shutterSound = if (flag) CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_ON else CameraManager.CameraShutterSound.CAMERA_SHUTTER_SOUND_OFF

        }
    }

    private fun toggleGrid() {
        bindView.buttonGrid.toggleButton(
                netGrid == CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN,
                180f,
                R.drawable.ic_grid_off,
                R.drawable.ic_grid_on
        ) { flag ->
            netGrid = if (flag) CameraManager.CameraNetGrid.CAMERA_NET_GRID_OPEN else CameraManager.CameraNetGrid.CAMERA_NET_GRID_CLOSE

        }
    }


    private var countDownJob: Job? = null

    /**
     * 开始倒计时
     */
    private fun countDownStart(count: Int, call: () -> Unit) {
        bindView.textCountDownTip.text = "自动拍照"
        countDownJob = lifecycleScope.launch(Main) {
            for (i in count downTo 1) {
                bindView.textCountDown.text = i.toString()
                delay(1000)
            }
            bindView.textCountDown.text = ""
            bindView.textCountDownTip.text = ""
            call.invoke()
        }

    }

    /**
     *取消倒计时
     */
    private fun countDownStop() {
        bindView.textCountDown.text = ""
        bindView.textCountDownTip.text = ""
        countDownJob?.cancel()
    }


    /** 音量键*/
    private fun keyVolumeDown() {
        if (cameraParameBuild.volumeKeyControl == 1) {
            captureImage()
        } else if (cameraParameBuild.volumeKeyControl == 2) {
            val len = cameraManager.cameraCurrentZoom - (cameraManager.cameraMaxZoom - cameraManager.cameraMinZoom) / 10
            showCameraZoomBar(if (len < 0) 0F else len)
        }
    }

    private fun keyVolumeUp() {
        if (cameraParameBuild.volumeKeyControl == 1) {
            captureImage()
        } else if (cameraParameBuild.volumeKeyControl == 2) {
            val len = cameraManager.cameraCurrentZoom + (cameraManager.cameraMaxZoom - cameraManager.cameraMinZoom) / 10
            showCameraZoomBar(if (len > cameraManager.cameraMaxZoom) cameraManager.cameraMaxZoom.toFloat() else len)
        }
    }

    private var zoomBarJob: Job? = null
    private fun showCameraZoomBar(zoom: Float) {
        zoomBarJob?.cancel()
        zoomBarJob = lifecycleScope.launch(Main) {
            cameraManager.setCameraZoom(zoom)
            bindView.rulerView.visiable()
            bindView.rulerView.value = zoom
            delay(5000)
            bindView.rulerView.gone()
        }
    }

    override fun lazyLoad() {

    }


    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(cameraInfoBroadcastReceiver)
        locationEngine = null
        bindCameraInit = false
        cameraManager.closeDriver()
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L
        private var locationEngineManager: LocationManagerEngine? = null
        private var locationIsEnable: MutableLiveData<Boolean> = MutableLiveData(false)
        fun setCameraXLocationEngine(locationManagerEngine: LocationManagerEngine?) {
            locationEngineManager = locationManagerEngine
            locationIsEnable.postValue(locationManagerEngine != null)
        }

        fun newInstance(cameraParameBuild: CameraParameBuild, sharedPreferencesName: String = "app_setting", locationManagerEngine: LocationManagerEngine? = null): BaseCameraFragment {
            return BaseCameraFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("data", cameraParameBuild)
                    putString("sharedPreferencesName", sharedPreferencesName)
                    locationEngine = locationManagerEngine?.createEngine()
                }
            }
        }
    }

    private var bindCameraInit = false
    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(volumeKeyControlBroadcast, IntentFilter().apply {
            addAction(KEYDOWN_VOLUME_KEY_ACTION)
        })
        if (allPermissionsGranted()) {
            onPermissionGranted()

        } else {
            bindView.viewFinder.unBindSurfaceView()
            bindCameraInit = false
            findNavController().navigate(BaseCameraFragmentDirections.actionCameraToPermissions(args.cameraData, args.sharedPreferencesName))

        }
        cameraSensorController.onResume()
    }

    private fun onPermissionGranted() {
        if (!bindCameraInit) {
            bindView.viewFinder.bindSurfaceView(cameraManager, cameraParameBuild, { result, error ->
                if (result) {
                    focusSucessCaptureImage()
                } else {
                    showError(error)
                }
            }, { zoom ->
                showCameraZoomBar(zoom)
            })
            updateCameraUi()
        } else {
            if (!cameraManager.getPreview()) {
                cameraManager.startPreview()
            }
        }
        bindCameraInit = true
        if (cameraParameBuild.lat <= 0.0 && cameraParameBuild.supportLocation) {
            if (null != locationEngineManager) {
                locationEngine = locationEngineManager?.createEngine()
                if (null != locationEngine) {
                    bindView.tvLocation.visiable()
                    getLocationInfo()
                }

            }

        }

        if (cameraData.supportMultiplePhoto && cameraData.cameraPhotoDatas.isNotEmpty()) {

            setGalleryThumbnail(cameraData.cameraPhotoDatas.last())
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                onPermissionGranted()
            } else {
                view?.let { v ->
                    Snackbar.make(v, "拍照没有这些权限将无法运行！", Snackbar.LENGTH_INDEFINITE)
                            .setAction("退出") { ActivityCompat.finishAffinity(requireActivity()) }
                            .show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(volumeKeyControlBroadcast)
        cameraSensorController.onPause()
        if (bindCameraInit && cameraManager.getPreview()) {
            cameraManager.stopPreview()
        }
    }

    // Check for the permissions
    private fun allPermissionsGranted() = cameraPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


}