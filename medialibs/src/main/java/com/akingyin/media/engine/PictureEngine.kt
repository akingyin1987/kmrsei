/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.engine

/**
 * 创建图片引擎库
 * @ Description:
 * @author king
 * @ Date 2020/7/14 11:32
 * @version V1.0
 */
interface PictureEngine {


    /**
     * Create ImageLoad Engine
     *
     * @return
     */
    fun createEngine(): ImageEngine


}