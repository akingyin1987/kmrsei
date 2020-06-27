/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.model;

import android.text.TextUtils;
import java.io.Serializable;
import java.util.List;

/**
 * 含类型的图文集合
 * Created by Administrator on 2016/10/10.
 */

public class ImageTextTypeModel implements Serializable {

    public   String    text;

    private List<ImageTextModel>   items;

    public List<ImageTextModel> getItems() {
        return items;
    }

    public void setItems(List<ImageTextModel> items) {
        this.items = items;
    }

    public ImageTextTypeModel(String text, List<ImageTextModel> items) {
        this.text = text;
        this.items = items;
    }

    public ImageTextTypeModel() {
    }

    @Override public String toString() {
        if(!TextUtils.isEmpty(text)){
            return  text;
        }
        return super.toString();
    }
}
