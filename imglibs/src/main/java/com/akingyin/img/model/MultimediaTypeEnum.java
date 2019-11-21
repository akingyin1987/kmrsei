/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.model;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #

 * @ Author king
 * @ Date 2016/12/3 11:28
 * @ Version V1.0
 */

public enum MultimediaTypeEnum {
  /**
   *
   */
  CustomCamera1("自定义相机1",0), SysCamrea("系统相机",1),CustomCamera2("自定义相机2",2)
  ,TuTuCamera("涂图相机",3),GoogleCamera("google相机",4),SysVideo("系统录像",5),CustomVideo("自定义录像",6)
  ,SysAudio("系统录音",8),CustomAudio("自定义录音",9),TuYa1("涂鸦1",10),TuYa2("涂鸦2",11);



  // 成员变量
  private String name;
  private int index;

  // 构造方法
  private MultimediaTypeEnum(String name, int index) {
    this.name = name;
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public static MultimediaTypeEnum getMultimediaType(int index) {
    for (MultimediaTypeEnum c : MultimediaTypeEnum.values()) {
      if (c.getIndex() == index) {
        return c;
      }
    }
    return null;
  }
}
