package com.cc.rnbridge.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.entity.BundleConfig;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

/**
 * @author liujc
 * @ClassName MyReactActivity
 * @date 2019/7/17
 * @Description RN页面基类
 */
public abstract class BaseReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    private ReactRootView mReactRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getBundleConfig() != null){
            mReactRootView = new ReactRootView(this);
            setContentView(mReactRootView);
            RNBridge.getInstance().setRootView(mReactRootView, getBundleConfig());
        }
    }

    protected abstract BundleConfig getBundleConfig();

    public String getModuleName(){
        if (getBundleConfig() != null && !TextUtils.isEmpty(getBundleConfig().getModuleName())){
            return getBundleConfig().getModuleName();
        }
        return "DEFAULT_BUNDLE";
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && getReactInstanceManager() != null) {
            getReactInstanceManager().showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getReactInstanceManager() != null) {
            getReactInstanceManager().onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getReactInstanceManager() != null) {
            getReactInstanceManager().onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getReactInstanceManager() != null) {
            getReactInstanceManager().onHostDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (getReactInstanceManager() != null) {
            getReactInstanceManager().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private ReactInstanceManager getReactInstanceManager(){
        return RNBridge.getInstance().getReactInstanceManager(getModuleName());
    }
}
