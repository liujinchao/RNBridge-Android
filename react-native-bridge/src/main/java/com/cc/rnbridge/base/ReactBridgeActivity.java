package com.cc.rnbridge.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.entity.BundleConfig;
import com.cc.rnbridge.util.UrlUtil;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

/**
 * @author liujc
 * @ClassName MyReactActivity
 * @date 2019/7/17
 * @Description RN渲染页面
 */
public class ReactBridgeActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    public static final String KEY_BUNDLE_CONFIG = "key_bundle_config";
    public static final String KEY_BUNDLE_ID = "bundleId";

    public static final String DEFAULT_RN_HOST = "rnbridge://app.native.com";

    protected BundleConfig mBundleConfig;
    private int mBundleId;
    private ReactRootView mReactRootView;

    /**
     * 跳转RN页面
     * @param mContext
     * @param targetUrl
     * 1. 如果app是继承ReactBridgeActivity实现的Activity页面，Scheme路由地址替换成自己的详细路由地址，如：scheme://host/path
     * 2. 如果app未继承ReactBridgeActivity，则详细路由为rnbridge://app.native.com/path
     * @param bundleConfig
     */
    public static void openRnUrl(Context mContext,
                                String targetUrl,
                                BundleConfig bundleConfig){
        if (TextUtils.isEmpty(targetUrl)){
            return;
        }
        if (bundleConfig == null){
            bundleConfig = new BundleConfig.BundleConfigBuild().build();
        }
        if (targetUrl.startsWith("/")){
            targetUrl = DEFAULT_RN_HOST + targetUrl;
        }
        UrlUtil.UrlEntity urlEntity = UrlUtil.toUrlBundle(targetUrl, bundleConfig.getAppProperties());
        targetUrl = urlEntity.baseUrl;
        Bundle appProperties = urlEntity.params;
        targetUrl = UrlUtil.toUrlParams(targetUrl, appProperties);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
        if (bundleConfig != null){
            if (appProperties != null && appProperties.size() > 0){
                bundleConfig.setAppProperties(appProperties);
            }
            intent.putExtra(KEY_BUNDLE_CONFIG, bundleConfig);
        }
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if (mBundleConfig != null && !isCustomRootView()){
            mReactRootView = new ReactRootView(this);
            setContentView(mReactRootView);
            RNBridge.getInstance().setRootView(mReactRootView, mBundleConfig);
        }
    }

    private void initData() {
        mBundleId = getBundleId();
        if (getBundleConfig() != null){
            mBundleConfig = getBundleConfig();
        }else {
            mBundleConfig = getBundleConfig(mBundleId);
            if (mBundleConfig == null){
                Toast.makeText(ReactBridgeActivity.this, "未查询到"+mBundleId+"对应的bundle配置信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 针对通过Scheme形式跳转到该目的页时，根据bundleId去本地缓存中查找对应的bundle文件配置信息
     * @param bundleId
     * @return
     */
    protected BundleConfig getBundleConfig(int bundleId){
        return null;
    }

    private int getBundleId() {
        Uri uri = getIntent().getData();
        String idStr = null;
        if (uri != null){
            idStr = uri.getQueryParameter(KEY_BUNDLE_ID);
        }
        if (!TextUtils.isEmpty(idStr)){
            return Integer.valueOf(idStr);
        }
        return 0;
    }

    /**
     * 是否自定义RN页面加载布局，true: 自定义，则不采用ReactBridgeActivity内部加载方式，false:采用默认方式
     * @return
     */
    protected Boolean isCustomRootView(){
        return false;
    }

    protected ReactRootView getReactRootView(){
        return mReactRootView;
    }

    protected BundleConfig getBundleConfig(){
        if (getIntent() != null
                && getIntent().hasExtra(KEY_BUNDLE_CONFIG)){
            return getIntent().getParcelableExtra(KEY_BUNDLE_CONFIG);
        }else {
            return null;
        }
    };

    public int getCurrentBundleId(){
        if (getBundleConfig() != null){
            return getBundleConfig().getBundleId();
        }
        return 0;
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
        return RNBridge.getInstance().getReactInstanceManager(getCurrentBundleId());
    }
}
