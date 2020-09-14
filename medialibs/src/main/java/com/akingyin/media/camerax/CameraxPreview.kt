/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.camerax

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.akingyin.media.R
import com.akingyin.media.camera.FouceView


/**
 * @ Description:
 * @author king
 * @ Date 2020/9/14 16:44
 * @version V1.0
 */
class CameraxPreview @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var camera_surface: TextureView
    var camera_fouce: FouceView
    var camera_img: ImageView
    init {
        View.inflate(context,R.layout.camerax_view , this).run {
            camera_fouce = findViewById(R.id.camera_fouce)
            camera_surface = findViewById(R.id.camera_surface)
            camera_img = findViewById(R.id.camera_img)
        }
    }
}