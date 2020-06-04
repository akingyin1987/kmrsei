/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.glide;

import com.bumptech.glide.load.engine.GlideException;

/**
 * 加载进度监听
 * @author king
 * @version V1.0
 * @ Description:
 *
 *
 * @ Date 2017/12/21 17:11
 */
public interface OnProgressListener {

    void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone,
        GlideException exception);
}
