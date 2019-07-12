package com.cc.rnbridge.entity;

import java.io.Serializable;

/**
 * @author liujc
 * @ClassName BaseEvent
 * @date 2019/7/11
 * @Description RN与Android通信的消息对象基类
 */
public class BaseEvent implements Serializable{

    private int code;
    private String msg;

    public BaseEvent() {
    }

    public BaseEvent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
