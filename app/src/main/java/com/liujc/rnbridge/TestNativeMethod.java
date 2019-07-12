package com.liujc.rnbridge;

import android.app.Activity;
import android.content.Intent;

import com.cc.rnbridge.base.BaseNativeMethod;
import com.cc.rnbridge.entity.Event;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

/**
 * @author liujc
 * @ClassName TestReactModule
 * @date 2019/7/10
 * @Description (这里用一句话描述这个类的作用)
 */
public class TestNativeMethod implements BaseNativeMethod {

    @Override
    public void startActivityForResult(Activity mActivity, String targetPath, Callback success, Callback error) {

    }

    @Override
    public void onActivityResult(Activity mActivity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void sendEvent(Activity mActivity, Event event, Promise promise) {
        if (event.getCode() == 200){
            promise.resolve("接收到RN消息："+ event.toString());
        }else if (event.getCode() == 500){
            promise.reject(new Throwable("不想处理RN消息"));
        }else {
            promise.reject("100", "服务的不满意，RN消息等等再说");
        }
    }
}
