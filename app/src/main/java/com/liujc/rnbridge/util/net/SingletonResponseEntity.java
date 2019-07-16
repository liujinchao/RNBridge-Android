package com.liujc.rnbridge.util.net;

import java.io.Serializable;

public class SingletonResponseEntity<T> implements Serializable{
    protected int code;
    protected String message;
    protected T data;

    public SingletonResponseEntity(int sc, String msg, T result) {
        this.data = result;
        this.message = msg;
        this.code = sc;
    }

    public SingletonResponseEntity() {
        this(200, null, null);
    }

    public SingletonResponseEntity(int sc, String msg) {
        this(sc, msg, null);
    }

    public SingletonResponseEntity(T data) {
        this(200, null, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
