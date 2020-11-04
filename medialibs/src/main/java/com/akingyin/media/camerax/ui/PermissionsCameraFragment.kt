/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.akingyin.media.R
import kotlinx.android.synthetic.main.fragment_no_camera_permissions.*
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest


private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
/**
 * @ Description:
 * @author king
 * @ Date 2020/9/15 11:01
 * @version V1.0
 */
class PermissionsCameraFragment : Fragment(){

     private val args: PermissionsCameraFragmentArgs by navArgs()

     lateinit var cameraPermissionsRequester:PermissionsRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("注册权限处理 ---->>>")
        cameraPermissionsRequester =  constructPermissionsRequest(Manifest.permission.CAMERA,
            onShowRationale = ::onCameraShowRationale,
            onPermissionDenied = ::onCameraDenied,
            onNeverAskAgain = ::onCameraNeverAskAgain) {
            gotoCameraFragment()
        }
        lifecycleScope.launchWhenResumed {
            println("进来了吗")
            cameraPermissionsRequester.launch()
        }
//        if(!hasPermissions(requireContext())){
//            registerForActivityResult(ActivityResultContracts.RequestPermission()){
//               if(it){
//                  gotoCameraFragment()
//               }else{
//                   Toast.makeText(context, "请允许打开相机权限！", Toast.LENGTH_LONG).show()
//               }
//            }.launch(Manifest.permission.CAMERA)
//        }else{
//            gotoCameraFragment()
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return inflater.inflate(R.layout.fragment_no_camera_permissions,container,false)
    }

    private fun  gotoCameraFragment(){
       findNavController().navigate(PermissionsCameraFragmentDirections.actionPermissionsToCamera(args.fileDir,args.fileName?:"",args.sharedPreferencesName))

    }
    private fun onCameraDenied() {
        tv_error.text = getString(R.string.permission_camera_denied)
        Toast.makeText(requireContext(), R.string.permission_camera_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onCameraShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onCameraNeverAskAgain() {
        tv_error.text = getString(R.string.permission_camera_never_ask_again)
        Toast.makeText(requireContext(), R.string.permission_camera_never_ask_again, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        println("权限取消---->")
        super.onDestroyView()
    }

    companion object{
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}