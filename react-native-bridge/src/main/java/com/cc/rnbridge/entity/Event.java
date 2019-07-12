package com.cc.rnbridge.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liujc
 * @ClassName Event
 * @date 2019/7/10
 * @Description RN与Android通信的事件对象
 */
public class Event<T> extends BaseEvent{
    /**
     * 传递的参数
     */
    private T data;

    public Event() {

    }

    public Event(int code, String name) {
        super(code, name);
    }

    public Event(int code, String name, T data) {
        super(code, name);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public <T> List<T> getParamsListEntity(Class<T> clazz) {
        if (!TextUtils.isEmpty(JSON.toJSONString(getData()))){
            return JSON.parseArray(JSON.toJSONString(getData()), clazz);
        }
        return new ArrayList<>();
    }

    public  <T> T getParamsEntity(Class<T> clazz) {
        if (!TextUtils.isEmpty(JSON.toJSONString(getData()))){
            return JSON.parseObject(JSON.toJSONString(getData()), clazz);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "code=" + getCode() +
                ",msg=" + getMsg() +
                ",data=" + JSON.toJSONString(getData()) +
                '}';
    }
}