/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */

public class ImageTextTypeList  implements Serializable {

    private List<ImageTextTypeModel>   items;

    public List<ImageTextTypeModel> getItems() {
        return items;
    }

    public void setItems(List<ImageTextTypeModel> items) {
        this.items = items;
    }
}
