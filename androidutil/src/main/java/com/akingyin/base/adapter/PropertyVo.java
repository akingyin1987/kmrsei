/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.adapter;

import com.akingyin.base.utils.DateUtil;
import com.akingyin.base.utils.StringUtils;
import java.io.Serializable;
import java.math.BigDecimal;


public class PropertyVo  implements Serializable {

	private static final long serialVersionUID = -6559043443805155972L;
	public    String    propertyName;

	public    String    propertyValue;

	public    String     image;

	public    String     video;

	public    String    propertyKey;

	public    int    linkify;


	public   PropertyVo(){
		
	}
	
	private   int  color;
	
	
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public   PropertyVo(String  propertyName,String  propertyValue){
		this.propertyName = propertyName;
		this.propertyValue = StringUtils.isEmptyOrNull(propertyValue);
	}
	
	
	public   PropertyVo(String  propertyName,String  propertyValue,int color){
		this.propertyName = propertyName;
		this.propertyValue = StringUtils.isEmptyOrNull(propertyValue);
		this.color = color;
	}
	public  PropertyVo(String  propertyName,Integer  propertyValue){
		this.propertyName = propertyName;
		this.propertyValue = null ==propertyValue?"":String.valueOf(propertyValue);
	}
	
	public  PropertyVo(String  propertyName,Long  propertyValue){
		this.propertyName = propertyName;
		this.propertyValue = null == propertyValue || propertyValue<=0?"": DateUtil.millis2String(propertyValue);

	}
	
	public  PropertyVo(String  propertyName,Double  propertyValue){
		this.propertyName = propertyName;
		this.propertyValue = null == propertyValue || propertyValue<=0?"":String.valueOf(propertyValue);
	}

	public PropertyVo(String propertyName, BigDecimal   propertyValue){
		this.propertyName = propertyName;
		this.propertyValue = null == propertyValue?"":String.valueOf(propertyValue.doubleValue());
	}

	public PropertyVo(String propertyName, String propertyValue, String image) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.image = image;
	}


}
