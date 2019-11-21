/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.model;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/12/27 13:51
 */

public class ValMessage {

  private   boolean   val = true;

  private   String   msg;

  public boolean isVal() {
    return val;
  }

  public void setVal(boolean val) {
    this.val = val;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public ValMessage(boolean val, String msg) {
    this.val = val;
    this.msg = msg;
  }

  public ValMessage() {
  }
}
