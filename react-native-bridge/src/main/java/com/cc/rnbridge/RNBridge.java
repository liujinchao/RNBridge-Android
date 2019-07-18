package com.cc.rnbridge;

import android.app.Application;

import com.cc.rnbridge.entity.BundleConfig;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final Map<String,ReactNativeHost> nativeHostHashMap = new HashMap<>();

    private static final Map<String,ReactInstanceManager> reactInstanceManagerHashMap = new HashMap<>();

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

    public void initRNConfig(Application mApplication,
                             boolean debug,
                             List<ReactPackage> reactPackages){
        this.mApplication = mApplication;
        this.debug = debug;
        this.reactPackages = reactPackages;
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
        this.mBundleConfig = bundleConfig;
        setRootView(getApplication(),
                mReactRootView,
                bundleConfig.getModuleName(),
                bundleConfig.getBundleFile(),
                bundleConfig.getBundleAssetName(),
                bundleConfig.getJsMainMoudlePath(),
                isDebug(),
                getReactPackages());
    }

    public void setRootView(Application mApplication,
                            ReactRootView mReactRootView,
                            String moduleName,
                            String bundleFile,
                            String bundleAssetName,
                            String jsMainMoudlePath,
                            boolean debug,
                            List<ReactPackage> reactPackages){
        mReactInstanceManager = getReactInstanceManager(moduleName);
        if (mReactInstanceManager == null){
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
            reactInstanceManagerHashMap.put(moduleName, mReactInstanceManager);
        }
        startReactApplication(mReactRootView, moduleName);
    }

    private void startReactApplication(ReactRootView mReactRootView, String moduleName){
        if (mReactRootView != null && mReactRootView.getReactInstanceManager() == null){
            mReactRootView.startReactApplication(getReactInstanceManager(moduleName), moduleName, null);
        }
    }

    public ReactInstanceManager getReactInstanceManager(String moduleName){
        return reactInstanceManagerHashMap.get(moduleName);
    }

    private ReactNativeHost mReactNativeHost;
    /**
     * 计划另一种渲染方式，在Application中实现，不继承BaseReactActivity
     * @param application
     * @return
     */
    @Deprecated
    public ReactNativeHost getReactNativeHost(Application application) {
        mReactNativeHost = nativeHostHashMap.get(mBundleConfig.getModuleName());
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
            nativeHostHashMap.put(mBundleConfig.getModuleName(), mReactNativeHost);
        }
        return mReactNativeHost;
    }

}
