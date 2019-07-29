package com.cc.rnbridge.base;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.cc.rnbridge.entity.BundleConfig;

/**
 * @author liujc
 * @ClassName BaseBridgeActivity
 * @date 2019/7/26
 * @Description RN中转页，在跳转RN页之前处理一些耗时操作
 */
public abstract class BaseBridgeActivity extends AppCompatActivity {

    //可在Scheme中带有该字段用于远程调试
    public static final String KEY_RN_MODULE_NAME_TEST = "CCBridgeTest";
    public static final int KEY_BUNDLE_ID_DEBUG = 10011;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentData();
    }

    private void initIntentData() {
        if (getIntent() == null){
            finishActivity();
            return;
        }
        Uri uri = getIntent().getData();
        if (debugBundle(uri)){
            finishActivity();
            return;
        }

        String targetUrl = null;
        if (uri != null){
            targetUrl = replaceRNBridgeHost(uri.toString());
        }
        handleEvent(targetUrl);
    }

    /**
     * 路由替换成RN页面渲染路由
     * @param targetUrl
     * @return
     */
    private String replaceRNBridgeHost(String targetUrl) {
        if (!TextUtils.isEmpty(getTargetScheme())){
            targetUrl = replaceRNBridgeHost(targetUrl, getTargetScheme());
        }else {
            targetUrl = replaceRNBridgeHost(targetUrl, ReactBridgeActivity.DEFAULT_RN_HOST);
        }
        return targetUrl;
    }

    private String replaceRNBridgeHost(String targetUrl, String targetScheme) {
        if (!TextUtils.isEmpty(targetUrl) && targetUrl.startsWith(getOriginScheme())){
           targetUrl = targetUrl.replace(getOriginScheme(), targetScheme);
        }
        return targetUrl;
    }

    /**
     * 如果debug远程调试，则根据moduleName直接跳转RN渲染页
     * @param uri
     * @return
     */
    private boolean debugBundle(Uri uri) {
        if (uri == null){
            return false;
        }
        String testModuleName = uri.getQueryParameter(KEY_RN_MODULE_NAME_TEST);
        if (!TextUtils.isEmpty(testModuleName)){
            BundleConfig mBundleConfig = new BundleConfig.BundleConfigBuild()
                    .setBundleId(KEY_BUNDLE_ID_DEBUG)
                    .setModuleName(testModuleName)
                    .build();
            String url = replaceRNBridgeHost(uri.toString(), ReactBridgeActivity.DEFAULT_RN_HOST);
            ReactBridgeActivity.openRnUrl(BaseBridgeActivity.this, url ,mBundleConfig);
            return true;
        }
        return false;
    }

    /**
     * 继承该Activity的页面Scheme路由
     * @return
     */
    protected abstract String getOriginScheme();

    /**
     * 将要替换成的目的页Scheme路由，为空则默认跳转RN渲染页 ReactBridgeActivity.DEFAULT_RN_HOST
     * @return
     */
    protected abstract String getTargetScheme();

    /**
     * 在该Activity的实现页根据Scheme Url处理相关耗时逻辑
     * @param targetUrl
     */
    protected abstract void handleEvent(String targetUrl);

    public void finishActivity(){
        finish();
    }

}
