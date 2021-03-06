package com.cc.rnbridge.moudles;

import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cc.rnbridge.base.BaseNativeMethod;
import com.cc.rnbridge.entity.Event;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nonnull;

/**
 * @author liujc
 * @ClassName BridgeNativeModule
 * @date 2019/7/10
 * @Description Android原生预留给RN的调用方法
 */
final class BridgeNativeModule extends ReactContextBaseJavaModule {

    public static final String DEFAULT_MODULE_NAME = "BridgeNativeModule";

    private BaseNativeMethod baseReactMethod;

    private String mNativeModuleName;

    public BridgeNativeModule(@Nonnull ReactApplicationContext reactContext,
                              @Nonnull BaseNativeMethod reactMethod,
                              @Nonnull String nativeModuleName) {
        super(reactContext);
        this.baseReactMethod = reactMethod;
        Assertions.assertNotNull(baseReactMethod, "还没有实现BaseNativeMethod");
        if (TextUtils.isEmpty(nativeModuleName)) {
            nativeModuleName = DEFAULT_MODULE_NAME;
        }
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
