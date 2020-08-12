/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.media.doodle.core

import android.view.MotionEvent

/**
 * @ Description:
 * @author king
 * @ Date 2020/8/12 17:21
 * @version V1.0
 */
class FlipVerticallyEvent :AbstractFlipEvent() {
    override fun getFlipDirection() = IDoodle.Flip.FLIP_VERTICALLY

    override fun onActionDown(stickerView: IDoodleShape, event: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onActionMove(stickerView: IDoodleShape, event: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onActionUp(stickerView: IDoodleShape, event: MotionEvent) {
        TODO("Not yet implemented")
    }
}