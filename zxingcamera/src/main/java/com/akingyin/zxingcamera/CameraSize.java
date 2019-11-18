package com.akingyin.zxingcamera;

import android.graphics.Point;


/**
 * Name: CameraSize
 * Author: akingyin
 * Email:
 * Comment: //TODO
 * Date: 2019-04-10 11:35
 */
public class CameraSize   {



    private    int    width;

    private    int    hight;

    private    boolean   defalultSize;

    private    boolean   checked;


    public CameraSize(int width, int hight, boolean defalultSize, boolean checked) {
        this.width = width;
        this.hight = hight;
        this.defalultSize = defalultSize;
        this.checked = checked;
    }

    public CameraSize() {
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDefalultSize() {
        return defalultSize;
    }

    public void setDefalultSize(boolean defalultSize) {
        this.defalultSize = defalultSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHight() {
        return hight;
    }

    public void setHight(int hight) {
        this.hight = hight;
    }


    @Override
    public String toString() {

        return CameraConfigurationUtils.getCameraResolutionScale(new Point(width,hight))+Math.max(width,hight)+"x"+Math.min(width,hight)+(defalultSize?"[默认]":"")+(checked?"[当前]":"");
    }
}
