package com.cc.rnbridge.base;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cc.rnbridge.entity.Event;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nonnull;

/**
 * @author liujc
 * @ClassName BaseNativeModule
 * @date 2019/7/10
 * @Description Android原生预留给RN的调用方法
 */
public class BaseNativeModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private BaseNativeMethod baseReactMethod;

    private String mNativeModuleName;

    public BaseNativeModule(@Nonnull ReactApplicationContext reactContext,
                            @Nonnull BaseNativeMethod reactMethod,
                            @Nonnull String nativeModuleName) {
        super(reactContext);
        this.baseReactMethod = reactMethod;
        this.mNativeModuleName = nativeModuleName;
    }

    @ReactMethod
    public void sendEvent(ReadableMap eventMap, Promise promise) {
        String event = JSON.toJSONString(eventMap.toHashMap());
        if (!TextUtils.isEmpty(event)) {
            try {
                Event<Object> data = JSON.parseObject(event, new TypeReference<Event<Object>>(){});
                baseReactMethod.sendEvent(getCurrentActivity(), data, promise);
            }catch (Exception ex){
                promise.reject(new Throwable("出现了非期望结果"));
            }
        }else {
            promise.reject(new Throwable("消息实体不能为空"));
        }
    }

    @ReactMethod
    public void startActivityForResult(String targetPath, Callback success, Callback error) {
        Activity mActivity = getCurrentActivity();
        baseReactMethod.startActivityForResult(mActivity, targetPath, success, error);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        baseReactMethod.onActivityResult(activity, requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Intent intent) {
        baseReactMethod.onNewIntent(intent);
    }

    @Nonnull
    @Override
    public String getName() {
        if (TextUtils.isEmpty(getNativeModuleName())
                && getCurrentActivity() != null) {
            Toast.makeText(getCurrentActivity(), "请初始化NativeModuleName,以便RN中正常使用", Toast.LENGTH_SHORT).show();
        }
        return getNativeModuleName();
    }

    public String getNativeModuleName() {
        return mNativeModuleName;
    }

}
