package com.cc.rnbridge.base;

import android.app.Activity;
import android.content.Intent;

import com.cc.rnbridge.entity.Event;
import com.facebook.react.bridge.Promise;

/**
 * @author liujc
 * @ClassName BaseNativeMethod
 * @date 2019/7/10
 * @Description 此处需调用方实现方法
 */
public interface BaseNativeMethod {

    void startActivityForResult(Activity mActivity, String targetPath, Promise promise);

    void onActivityResult(Activity mActivity, int requestCode, int resultCode, Intent data);

    void onNewIntent(Intent intent);

    void sendEvent(Activity mActivity, Event event, Promise promise);

}
