package com.cc.rnbridge.util;

import android.util.Log;

import com.cc.rnbridge.entity.Event;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:  RNEventEmitter
 * @author: liujc
 * @date: 2019/6/5
 * @Description: 原生给RN发送消息
 * RN端接收方法如下:
 * DeviceEventEmitter.addListener(eventName, (msg) => {
 *     console.warn("接收原生信息："+JSON.stringify(msg));
 * })
 */
public class RNEventEmitter {

    private static final String TAG = "RNEventEmitter";
    public static final String DEFAULT_EVENT_NAME = "RNEventEmitter";

    // 在NativeModules()初始化调用
    private static ReactApplicationContext mReactContext;

    public static void setReactContext(ReactApplicationContext mReactContext) {
        RNEventEmitter.mReactContext = mReactContext;
    }

    public static void sendEvent(Event event) {
        sendEvent(DEFAULT_EVENT_NAME, RNMapUtil.toWritableMap(event));
    }

    public static void sendEvent(String eventName) {
        sendEvent(eventName, "");
    }

    public static void sendEvent(String eventName, List list) {
        if (list != null && list.size() > 0){
            sendEvent(eventName, list.toArray());
        }else {
            sendEvent(eventName, "");
        }
    }

    public static void sendEvent(String eventName, Map<String, Object> map) {
        sendEvent(eventName, RNMapUtil.toWritableMap(map));
    }

    public static void sendEvent(String eventName, Object[] array) {
        sendEvent(eventName, RNArrayUtil.toWritableArray(array));
    }

    public static void sendEvent(String eventName, Object msg) {
        sendEvent(mReactContext, eventName, msg);
    }

    private static void sendEvent(ReactApplicationContext reactContext, String eventName, WritableMap params) {
        if (reactContext == null) {
            Log.e(TAG, "ReactContext is null");
            throw new NullPointerException("ReactContext is null");
        }
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private static void sendEvent(ReactApplicationContext reactContext, String eventName, Object params) {
        if (reactContext == null) {
            Log.e(TAG, "ReactContext is null");
            throw new NullPointerException("ReactContext is null");
        }
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
