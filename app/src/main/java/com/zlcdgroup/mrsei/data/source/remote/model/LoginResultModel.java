package com.zlcdgroup.mrsei.data.source.remote.model;

import java.io.Serializable;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/10/8 15:18
 */
public class LoginResultModel implements Serializable {
  private static final long serialVersionUID = -5670665609000715748L;

  private   String     name;

  private   String     userId;

  private   int     age;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }
}
