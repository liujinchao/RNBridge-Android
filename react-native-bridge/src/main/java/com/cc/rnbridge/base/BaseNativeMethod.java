package com.cc.rnbridge.base;

import android.app.Activity;

import com.cc.rnbridge.entity.Event;
import com.facebook.react.bridge.Promise;

/**
 * @author liujc
 * @ClassName BaseNativeMethod
 * @date 2019/7/10
 * @Description 此处需调用方实现方法
 */
public interface BaseNativeMethod {

    void sendEvent(Activity mActivity, Event event, Promise promise);

}
