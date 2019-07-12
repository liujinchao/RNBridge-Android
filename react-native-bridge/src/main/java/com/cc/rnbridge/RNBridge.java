package com.cc.rnbridge;

import android.app.Application;
import android.text.TextUtils;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.uimanager.UIImplementationProvider;

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
//        SoLoader.init(mApplication, /* native exopackage */ false);
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

    public void setRootView(ReactRootView mReactRootView,
                             String moduleName){
        setRootView(mReactRootView, moduleName, null);
    }

    public void setRootView(ReactRootView mReactRootView,
                            String moduleName,
                            String bundleAssetName){
        setRootView(mReactRootView, moduleName, bundleAssetName, null);
    }

    public void setRootView(ReactRootView mReactRootView,
                            String moduleName,
                            String bundleAssetName,
                            String jsMainMoudlePath){
        setRootView(getApplication(), mReactRootView, moduleName, bundleAssetName, jsMainMoudlePath, isDebug(), getReactPackages());
    }

    public void setRootView(Application mApplication,
                            ReactRootView mReactRootView,
                            String moduleName,
                            boolean debug,
                            List<ReactPackage> reactPackages){
        setRootView(mApplication, mReactRootView, moduleName, null,null, debug, reactPackages);
    }

    public void setRootView(Application mApplication,
                            ReactRootView mReactRootView,
                            String moduleName,
                            String bundleAssetName,
                            String jsMainMoudlePath,
                            boolean debug,
                            List<ReactPackage> reactPackages){
        setRootView(mApplication, mReactRootView, moduleName, null, bundleAssetName, jsMainMoudlePath, debug, reactPackages);
    }

    public void setRootView(Application mApplication,
                            ReactRootView mReactRootView,
                            String moduleName,
                            String bundleFile,
                            String bundleAssetName,
                            String jsMainMoudlePath,
                            boolean debug,
                            List<ReactPackage> reactPackages){
        if (getReactInstanceManager() == null){
            ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                    .setApplication(mApplication)
                    .setUseDeveloperSupport(debug)
                    .setJSMainModulePath(getJSMainModuleName(jsMainMoudlePath))
                    .setUIImplementationProvider(getUIImplementationProvider())
                    .setInitialLifecycleState(LifecycleState.BEFORE_CREATE);

            String jsBundleFile = getJSBundleFile(bundleFile);
            if (jsBundleFile != null) {
                builder.setJSBundleFile(jsBundleFile);
            } else {
                builder.setBundleAssetName(Assertions.assertNotNull(getBundleAssetName(bundleAssetName)));
            }
            if (reactPackages != null && reactPackages.size() > 0){
                builder.addPackages(reactPackages);
            }
            mReactInstanceManager = builder.build();
        }
        startReactApplication(mReactRootView, moduleName);
    }

    private void startReactApplication(ReactRootView mReactRootView, String moduleName){
        if (mReactRootView != null && mReactRootView.getReactInstanceManager() == null){
            mReactRootView.startReactApplication(getReactInstanceManager(), moduleName, null);
        }
    }
    private UIImplementationProvider getUIImplementationProvider() {
        return new UIImplementationProvider();
    }

    private String getBundleAssetName(String bundleAssetName) {
        if (!TextUtils.isEmpty(bundleAssetName)){
            return bundleAssetName;
        }
        return "index.android.bundle";
    }

    private String getJSBundleFile(String bundleFile) {
        return bundleFile;
    }

    private String getJSMainModuleName(String jsMainMoudlePath) {
        if (!TextUtils.isEmpty(jsMainMoudlePath)){
            return jsMainMoudlePath;
        }
        return "index.android";
    }

    public ReactInstanceManager getReactInstanceManager(){
        return mReactInstanceManager;
    }
}
