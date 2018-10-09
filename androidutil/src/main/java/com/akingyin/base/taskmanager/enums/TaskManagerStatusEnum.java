/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.taskmanager.enums;

/**
 * 任务管理状态
 * Created by Administrator on 2016/7/3.
 */
public enum  TaskManagerStatusEnum {

    NULL(1,"无"),DOING(2,"进行中"),CANCEL(3,"已取消"),COMPLETE(4,"已完成")
    ,NETError(5,"网络错误");




    private String name;
    private int code;

    TaskManagerStatusEnum(int   code, String  name){
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public static TaskManagerStatusEnum getTaskManagerStatus(int code) {
        for (TaskManagerStatusEnum c : TaskManagerStatusEnum.values()) {
            if (c.getCode() == code) {
                return c;
            }
        }
        return null;
    }
}
