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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.akingyin.media.R

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
/**
 * @ Description:
 * @author king
 * @ Date 2020/9/15 11:01
 * @version V1.0
 */
class PermissionsCameraFragment : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!hasPermissions(requireContext())){
            registerForActivityResult(ActivityResultContracts.RequestPermission()){
               if(it){
                  gotoCameraFragment()
               }else{
                   Toast.makeText(context, "请允许打开相机权限！", Toast.LENGTH_LONG).show()
               }
            }.launch(Manifest.permission.CAMERA)
        }else{
            gotoCameraFragment()
        }
    }

   private fun  gotoCameraFragment(){
        val  fileName =arguments?.getString("fileName","")?:""
        val  fileDir = arguments?.getString("fileDir","")?:""
        if(fileName.isNotEmpty() and fileDir.isNotEmpty()){
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(PermissionsCameraFragmentDirections.actionPermissionsToCamera(fileDir,fileName))

        }else{
            val map = NavHostFragment.findNavController(this).graph.arguments
            val bundle = map["filePath"]?.defaultValue?.let {
                it as Bundle
            }
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(PermissionsCameraFragmentDirections.actionPermissionsToCamera(bundle?.getString("fileDir")?:"",bundle?.getString("fileName")?:""))
        }

    }


    companion object{
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}