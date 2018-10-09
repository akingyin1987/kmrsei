package com.akingyin.base.taskmanager.enums;

/**
 * Created by Administrator on 2017/12/29.
 */

public enum ThreadTypeEnum {
    MainThread(1,"主线程"),CurrentThread(2,"当前线程"),IoThread(3,"IO线程");



    ThreadTypeEnum(int   code, String  name){
        this.code = code;
        this.name = name;
    }
    private String name;
    private int code;

}
