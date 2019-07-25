package com.liujc.rnbridge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.cc.rnbridge.base.ReactBridgeActivity;
import com.cc.rnbridge.entity.BundleConfig;
import com.cc.rnbridge.util.RNBundleUtil;
import com.liujc.rnbridge.util.BundleVersionInfo;
import com.liujc.rnbridge.util.net.CommonService;
import com.liujc.rnbridge.util.net.NetHelper;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.cc.rnbridge.base.ReactBridgeActivity.openRnUrl;

/**
 * @ClassName:  BundleUpdateActivity
 * @author: liujc
 * @date: 2019/7/24
 * @Description: RN bundle版本检查中转页, 在客户端统一处理bundle版本检查处理
 */
public class BundleUpdateActivity extends AppCompatActivity {

    public static final int KEY_BUNDLE_ONE = 1001;
    public static final int KEY_BUNDLE_TWO = 1002;

    //远程debug调试的budleId
    public static final int KEY_BUNDLE_ID_DEBUG = 10011;

    public static final String KEY_BUNDLE_ID = "bundleId";
    public static final String DEFAULT_RN_HOST = "rnbridge://app.liujc.com";
    public static final String KEY_APP_PROPERTIES = "appProperties";

    private BundleConfig mBundleConfig;
    private Integer mBundleId;
    private String targetUrl;

    /**
     * 新的启动RN方式，需携带bundleId
     * @param mContext
     * @param targetUrl RN跳转路由：rnbridge://app.liujc.com/path
     * @param appProperties
     */
    public static void openRNBundle(Context mContext, String targetUrl, Bundle appProperties){

        if (!targetUrl.startsWith(BundleUpdateActivity.DEFAULT_RN_HOST)){
            targetUrl = BundleUpdateActivity.DEFAULT_RN_HOST+targetUrl;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
        if (appProperties != null){
            intent.putExtra(KEY_APP_PROPERTIES, appProperties);
        }
        mContext.startActivity(intent);
    }

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

        String idStr = null;
        if (uri != null){
            idStr = uri.getQueryParameter(KEY_BUNDLE_ID);
            targetUrl = replaceRNBridgeHost(uri.toString());
        }
        if (!TextUtils.isEmpty(idStr)){
            mBundleId = Integer.valueOf(idStr);
        }
        mBundleConfig = loadRemoteBundle(mBundleId);
        if (mBundleConfig != null){
            if (getIntent().getBundleExtra(KEY_APP_PROPERTIES) != null){
                mBundleConfig.setAppProperties(getIntent().getBundleExtra(KEY_APP_PROPERTIES));
            }
            updateJsBundle();
        }
    }

    /**
     * 路由替换成RN页面渲染路由
     * @param targetUrl
     * @return
     */
    private String replaceRNBridgeHost(String targetUrl) {
        if (!TextUtils.isEmpty(targetUrl)){
            targetUrl = targetUrl.replace(DEFAULT_RN_HOST, ReactBridgeActivity.DEFAULT_RN_HOST);
        }
        return targetUrl;
    }

    private boolean debugBundle(Uri uri) {
        if (uri == null){
            return false;
        }
        String url = replaceRNBridgeHost(uri.toString());
        String testModuleName = uri.getQueryParameter("BridgeTest");
        if (!TextUtils.isEmpty(testModuleName)){
            mBundleConfig = new BundleConfig.BundleConfigBuild()
                    .setBundleId(KEY_BUNDLE_ID_DEBUG)
                    .setModuleName(testModuleName)
                    .build();
            ReactBridgeActivity.openRnUrl(BundleUpdateActivity.this, url ,mBundleConfig);
            return true;
        }
        return false;
    }

    /**
     * 模拟加载两个bundle，然后进行版本检查
     * @param bundleId
     * @return
     */
    public BundleConfig loadRemoteBundle(Integer bundleId){
        // TODO: 2019/7/25 此处模拟从本地缓存中根据bundleId获取对应的bundle配置信息
        Bundle bundle = new Bundle();
        bundle.putInt("bundleId",bundleId);
        if (bundleId == KEY_BUNDLE_ONE){
            BundleConfig bundleConfig = new BundleConfig.BundleConfigBuild()
                    .setBundleId(bundleId)
                    .setBundleVersion("1.0.1")
                    .setModuleName("rnTest1")
                    .setBundleAssetName("rnbundleone.bundle")
                    .setJsMainMoudlePath("rnTestOne")
                    .setAppProperties(bundle)
                    .build();
            return bundleConfig;
        }else if (bundleId == KEY_BUNDLE_TWO){
            BundleConfig bundleConfig = new BundleConfig.BundleConfigBuild()
                    .setBundleId(bundleId)
                    .setBundleVersion("1.0.1")
                    .setModuleName("rnTest2")
                    .setBundleAssetName("rnbundletwo.bundle")
                    .setJsMainMoudlePath("rnTestTwo")
                    .setAppProperties(bundle)
                    .build();
            return bundleConfig;
        }
        return null;
    }

    private void updateJsBundle(){
        if (!RNBundleUtil.checkPermission(BundleUpdateActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001)){
            return;
        }
//        testLocalUpdate();
        mockBundleUpdate();
    }

    /**
     * 本地模拟jsbundle的版本检查操作（本地服务，可能此路不同）
     */
    private void mockBundleUpdate() {
        NetHelper.getApiService(CommonService.class)
                .checkBundleVersion(mBundleConfig.getBundleId(), BuildConfig.VERSION_NAME, "1.0", mBundleConfig.getBundleVersion())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    if (responseEntity.getCode() == 200){
                        BundleVersionInfo bundleInfo = responseEntity.getData();
                        if (bundleInfo.isHasUpdate()){
                            if (bundleInfo.isCanUse()){
                                showMsg("正在下载更新");
                                downLoadBundle(bundleInfo.getBundleUrl());
                            }else {
                                showMsg("新功能正在开发中，敬请期待");
                                finishActivity();
                            }
                        }else if (bundleInfo.isCanUse()){
                            showMsg("当前已是最新版本");
                            // TODO: 2019/7/25  此处可以替换成  mBundleConfig.getBundleFilePath()
                            loadBundle(getBundleFilePath());
                        }else {
                            showMsg("新功能正在开发中，敬请期待");
                            finishActivity();
                        }
                    }
                }, throwable -> {
                    showMsg(throwable.getLocalizedMessage());
                    finishActivity();
                });
    }

    private String getBundleFilePath() {
        // TODO: 2019/7/25 说明已下载过最新bundle文件，且把对应bundleId的本地路径已存储在本地缓存中
        if (!TextUtils.isEmpty(mBundleConfig.getBundleFilePath())){
            return mBundleConfig.getBundleFilePath();
        }
        // TODO: 2019/7/25 此处默认zip包和的名称和bundle的名称一致，路径可自定义
        return getExternalCacheDir()+"/finalbundle/"+mBundleConfig.getBundleAssetName()+ "/" + mBundleConfig.getBundleAssetName();
    }

    private void showMsg(String msg){
        Toast.makeText(BundleUpdateActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void downLoadBundle(String bundleUrl) {
        RNBundleUtil.getInstance().downLoadBundle(bundleUrl, path -> loadBundle(path+"/"+mBundleConfig.getBundleAssetName()));
    }

    private void loadBundle(String bundleFilePath){
        // TODO: 2019/7/25 下载成功后此处需要根据bundleId更新本地缓存中的bundle配置信息
        File bundleFile = new File(bundleFilePath);
        String bundlePath = null;
        if(bundleFile.exists()){
            bundlePath = bundleFile.getAbsolutePath();
        }
        mBundleConfig.setBundleFilePath(bundlePath);
        openRnUrl(BundleUpdateActivity.this, targetUrl ,mBundleConfig);
        finishActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001){
            updateJsBundle();
        }
    }

    private void finishActivity(){
        finish();
    }
}
