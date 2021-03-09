/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui


import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog

import com.afollestad.materialdialogs.customview.customView
import com.akingyin.base.ext.click
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.ActivityHorizontalVerticalTestBinding



/**
 * @ Description:
 * @author king
 * @ Date 2021/2/26 11:37
 * @version V1.0
 */
class HorizontalVerticalTestDialogActivity : AppCompatActivity() {

    lateinit var viewBinding : ActivityHorizontalVerticalTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHorizontalVerticalTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.btnChange.click {
            when(resources.configuration.orientation){
                Configuration.ORIENTATION_LANDSCAPE -> {
                    viewBinding.tvInfo.text = "当前为竖屏"
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                }
                Configuration.ORIENTATION_PORTRAIT -> {
                    viewBinding.tvInfo.text = "当前为横屏"
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                }

                Configuration.ORIENTATION_SQUARE -> {

                }
                Configuration.ORIENTATION_UNDEFINED -> {

                }
            }
        }

        viewBinding.btnDialog.click {

           // BottomSheet
          val dialog =  MaterialDialog(this).show {

                customView(R.layout.dialog_test_hori_vertical, scrollable = true)
                title(text = "测试对话框用的横竖屏界面")
                negativeButton(text = "取消")
                positiveButton(text = "确定")
                cancelOnTouchOutside(false)
              //  cornerRadius(16f)
                // Using a dimen instead is encouraged as it's easier to have all instances changeable from one place
               // cornerRadius(res = R.dimen.my_corner_radius)
               // setPeekHeight(res = R.dimen.my_default_peek_height)

            }

            val lp = WindowManager.LayoutParams()
                    .apply {
                        copyFrom(dialog.window?.attributes)
                        width = WindowManager.LayoutParams.MATCH_PARENT
                        height = WindowManager.LayoutParams.MATCH_PARENT
                        //gravity = Gravity.CENTER
                    }
            dialog.window?.attributes = lp
           // dialog.window?.windowManager?.getWidthAndHeight()
        }
    }

    override fun onResume() {
        super.onResume()
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_LANDSCAPE -> {
                viewBinding.tvInfo.text = "当前为横屏"
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                viewBinding.tvInfo.text = "当前为竖屏"
            }

            Configuration.ORIENTATION_SQUARE -> {

            }
            Configuration.ORIENTATION_UNDEFINED -> {

            }
        }
    }
}