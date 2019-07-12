package com.cc.rnbridge.base;

import com.cc.rnbridge.util.RNEventEmitter;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author liujc
 * @ClassName BaseReactPackage
 * @date 2019/7/10
 * @Description RN包管理器
 */
public abstract class BaseReactPackage implements ReactPackage {

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactContext) {
        RNEventEmitter.setReactContext(reactContext);
        List<NativeModule> nativeModules = new ArrayList<>();
        nativeModules.add(new BaseNativeModule(reactContext, getNativeMethod(), getBridgeModuleName()));
        List<NativeModule> tmpModules = getNativeModules(reactContext);
        if (tmpModules != null && tmpModules.size() > 0){
            nativeModules.addAll(tmpModules);
        }
        return nativeModules;
    }

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        if (getViewManagers() != null){
            return getViewManagers();
        }
        return Arrays.asList();
    }

    protected abstract List<NativeModule> getNativeModules(ReactApplicationContext reactContext);

    protected abstract List<ViewManager> getViewManagers();

    protected abstract BaseNativeMethod getNativeMethod();

    protected abstract String getBridgeModuleName();

}
