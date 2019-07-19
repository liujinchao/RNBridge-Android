package com.cc.rnbridge;

import android.app.Application;
import android.os.Bundle;
import android.util.SparseArray;

import com.cc.rnbridge.base.ReactBridgeActivity;
import com.cc.rnbridge.entity.BundleConfig;
import com.cc.rnbridge.impl.BridgeConfig;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import java.util.Arrays;
import java.util.List;

/**
 * @author liujc
 * @ClassName RNBridge
 * @date 2019/6/6
 * @Description RN与原生桥接器
 */
public class RNBridge {

    private ReactInstanceManager mReactInstanceManager;

    private static volatile RNBridge defaultInstance;

    private Application mApplication;
    private boolean debug;
    private List<ReactPackage> reactPackages;
    private BundleConfig mBundleConfig = new BundleConfig();

    private static final SparseArray<ReactNativeHost> nativeHostArray = new SparseArray();

    /**
     * 暂时意义不大，为了下载的bundle及时生效，没有从缓存中读取ReactInstanceManager
     */
    private static final SparseArray<ReactInstanceManager> reactInstanceManagerArray = new SparseArray();

    private RNBridge(){

    }

    public static RNBridge getInstance() {
        if (defaultInstance == null) {
            synchronized (RNBridge.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RNBridge();
                }
            }
        }
        return defaultInstance;
    }

    public RNBridge initRNConfig(Application mApplication, BridgeConfig bridgeConfig){
        this.mApplication = mApplication;
        this.debug = bridgeConfig.isDebug();
        this.reactPackages = bridgeConfig.getReactPackages();
        return this;
    }

    public Application getApplication() {
        return mApplication;
    }

    public boolean isDebug() {
        return debug;
    }

    public List<ReactPackage> getReactPackages() {
        if (reactPackages == null){
            reactPackages = Arrays.asList();
        }
        return reactPackages;
    }

    public void setRootView(ReactRootView mReactRootView, BundleConfig bundleConfig){
        Assertions.assumeNotNull(getApplication(), "还没有初始化RNBridge");
        this.mBundleConfig = bundleConfig;
        setRootView(getApplication(),
                null,
                null,
                mReactRootView,
                bundleConfig.getBundleId(),
                bundleConfig.getModuleName(),
                bundleConfig.getBundleFilePath(),
                bundleConfig.getBundleAssetName(),
                bundleConfig.getJsMainMoudlePath(),
                bundleConfig.getAppProperties(),
                isDebug(),
                getReactPackages());
    }

    /**
     * 前两个字段计划延迟加载bundle文件时绑定Activity与RN生命周期
     * @param mActivity
     * @param defaultHardwareBackBtnHandler
     * @param mReactRootView
     * @param bundleConfig
     */
    @Deprecated
    public void setRootView(ReactBridgeActivity mActivity,
                            DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler,
                            ReactRootView mReactRootView,
                            BundleConfig bundleConfig){
        Assertions.assumeNotNull(getApplication(), "还没有初始化RNBridge");
        this.mBundleConfig = bundleConfig;
        setRootView(getApplication(),
                mActivity,
                defaultHardwareBackBtnHandler,
                mReactRootView,
                bundleConfig.getBundleId(),
                bundleConfig.getModuleName(),
                bundleConfig.getBundleFilePath(),
                bundleConfig.getBundleAssetName(),
                bundleConfig.getJsMainMoudlePath(),
                bundleConfig.getAppProperties(),
                isDebug(),
                getReactPackages());
    }

    private void setRootView(Application mApplication,
                             ReactBridgeActivity mActivity,
                             DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler,
                             ReactRootView mReactRootView,
                             int bundleId,
                             String moduleName,
                             String bundleFile,
                             String bundleAssetName,
                             String jsMainMoudlePath,
                             Bundle appProperties,
                             boolean debug,
                             List<ReactPackage> reactPackages){

        /*为了下载的bundle及时生效，参数重新组装*/
        ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                .setApplication(mApplication)
                .setUseDeveloperSupport(debug)
                .setJSMainModulePath(jsMainMoudlePath)
                .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);

        if (bundleFile != null) {
            builder.setJSBundleFile(bundleFile);
        } else {
            builder.setBundleAssetName(bundleAssetName);
        }
        if (reactPackages != null && reactPackages.size() > 0){
            builder.addPackages(reactPackages);
        }
        mReactInstanceManager = builder.build();
        if (mActivity != null){
            if (defaultHardwareBackBtnHandler != null){
                mReactInstanceManager.onHostResume(mActivity, defaultHardwareBackBtnHandler);
            }else {
                mReactInstanceManager.onHostResume(mActivity);
            }
        }
        reactInstanceManagerArray.put(bundleId, mReactInstanceManager);
        startReactApplication(mReactRootView, moduleName,appProperties);
    }

    private void startReactApplication(ReactRootView mReactRootView, String moduleName,Bundle appProperties){
        if (mReactRootView != null && mReactRootView.getReactInstanceManager() == null){
            mReactRootView.startReactApplication(mReactInstanceManager, moduleName, appProperties);
        }
    }

    public ReactInstanceManager getReactInstanceManager(int bundleId){
        return reactInstanceManagerArray.get(bundleId);
    }

    private ReactNativeHost mReactNativeHost;
    /**
     * 计划另一种渲染方式，在Application中实现，继承ReactActivity场景使用
     * @param application
     * @return
     */
    @Deprecated
    public ReactNativeHost getReactNativeHost(Application application) {
        mReactNativeHost = nativeHostArray.get(mBundleConfig.getBundleId());
        if (mReactNativeHost == null){
            mReactNativeHost = new ReactNativeHost(application) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return isDebug();
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    return getReactPackages();
                }
            };
            nativeHostArray.put(mBundleConfig.getBundleId(), mReactNativeHost);
        }
        return mReactNativeHost;
    }

}
