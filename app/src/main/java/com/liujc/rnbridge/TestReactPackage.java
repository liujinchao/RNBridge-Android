package com.liujc.rnbridge;

import com.cc.rnbridge.base.BaseNativeMethod;
import com.cc.rnbridge.base.BaseReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author liujc
 * @ClassName TestReactPackage
 * @date 2019/7/10
 * @Description (这里用一句话描述这个类的作用)
 */
public class TestReactPackage extends BaseReactPackage {

    @Override
    protected List<NativeModule> getNativeModules(ReactApplicationContext reactContext) {
        return Arrays.asList();
    }

    @Override
    protected List<ViewManager> getViewManagers() {
        return Arrays.asList();
    }

    @Override
    protected BaseNativeMethod getNativeMethod() {
        return new TestNativeMethod();
    }

    @Override
    protected String getBridgeModuleName() {
        return "CCNativeModule";
    }
}
