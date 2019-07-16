package com.liujc.rnbridge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.util.RNBundleUtil;
import com.facebook.react.ReactRootView;
import com.liujc.rnbridge.util.BundleVersionInfo;
import com.liujc.rnbridge.util.net.CommonService;
import com.liujc.rnbridge.util.net.NetHelper;

import java.io.File;

import javax.annotation.Nonnull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author liujc
 * @ClassName RN2Activity
 * @date 2019/7/13
 * @Description (这里用一句话描述这个类的作用)
 */
public class RNTest3Activity extends AppCompatActivity {
    private ReactRootView mReactRootView;

    public static final String KEY_BUNDLE_ID = "bundleId";
    public static final String KEY_BUNDLE_VERSION = "bundleVersion";
    public static final String KEY_MODULE_NAME = "moduleName";
    public static final String KEY_BUNDLE_ASSET_NAME = "bundleAssetName";
    public static final String KEY_JSMAIN_MOUDLE_PATH = "jsMainMoudlePath";

    public static final int KEY_BUNDLE_ONE = 1001;
    public static final int KEY_BUNDLE_TWO = 1002;


    private Integer bundleId;
    private String moduleName;
    private String bundleFilePath;
    private String bundleAssetName;
    private String jsMainMoudlePath;
    private String currentBundleVersion;

    public static void startActivity(Context context){
        startActivity(context,1003,BuildConfig.BUNDLE_THREE_VERSION,"rnTest3","rnTestThree.bundle", "rnTestThree");
    }

    public static void startActivity(Context context, Integer bundleId, String bundleVersion){
        if (bundleId == KEY_BUNDLE_ONE){
            startActivity(context, bundleId,
                    bundleVersion,
                    "rnTest1",
                    "rnbundleone.bundle",
                    "rnTestOne");
        }else if (bundleId == KEY_BUNDLE_TWO){
            startActivity(context, bundleId,
                    bundleVersion,
                    "rnTest2",
                    "rnbundletwo.bundle",
                    "rnTestTwo" );
        }

    }

    public static void startActivity(Context context,
                                     @Nonnull Integer bundleId,
                                     @Nonnull String bundleVersion,
                                     @Nonnull String moduleName,
                                     @Nonnull String bundleAssetName,
                                     @Nonnull String jsMainMoudlePath){
        Intent intent = new Intent(context, RNTest3Activity.class);
        intent.putExtra(KEY_BUNDLE_ID, bundleId);
        intent.putExtra(KEY_BUNDLE_VERSION, bundleVersion);
        intent.putExtra(KEY_MODULE_NAME, moduleName);
        intent.putExtra(KEY_BUNDLE_ASSET_NAME, bundleAssetName);
        intent.putExtra(KEY_JSMAIN_MOUDLE_PATH, jsMainMoudlePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rn3_layout);
        initIntentData();
        initView();
    }

    private void initIntentData() {
        bundleId = getIntent().getIntExtra(KEY_BUNDLE_ID, 0);
        currentBundleVersion = getIntent().getStringExtra(KEY_BUNDLE_VERSION);
        moduleName = getIntent().getStringExtra(KEY_MODULE_NAME);
        bundleAssetName = getIntent().getStringExtra(KEY_BUNDLE_ASSET_NAME);
        jsMainMoudlePath = getIntent().getStringExtra(KEY_JSMAIN_MOUDLE_PATH);
        bundleFilePath = getIntent().getStringExtra(KEY_JSMAIN_MOUDLE_PATH);
    }

    private void initView() {
        mReactRootView = findViewById(R.id.rrv_rn);
//        RNBridge.getInstance().setRootView(mReactRootView, "rnTest3","index.androidtestT.bundle");
//        RNBridge.getInstance().setRootView(mReactRootView, "rnTest3","rnTestThree.bundle","rnTestThree");

//        loadBundle();
//        updateJsBundle();
        testLocalUpdate();
    }

    private void loadBundle(){
        File bundleFile = new File(getExternalCacheDir()+"/finalbundle/finalbundle","rnTestThree.bundle");
        String bundlePath = null;
        if(bundleFile.exists()){
            bundlePath = bundleFile.getAbsolutePath();
        }
        RNBridge.getInstance().setRootView(mReactRootView, "rnTest3", bundlePath,"rnTestThree.bundle","rnTestThree");
    }

    private void updateJsBundle(){
//        String currentBundleVersion = SpUtil.getBundleVersion(RNTest3Activity.this, bundleId);
        if (!RNBundleUtil.checkPermission(RNTest3Activity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001)){
            return;
        }
//            testLocalUpdate();

        NetHelper.getApiService(CommonService.class)
                .checkBundleVersion(bundleId, BuildConfig.VERSION_NAME, "1.0", currentBundleVersion)
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
                            }
                        }else if (bundleInfo.isCanUse()){
                            showMsg("当前已是最新版本");
                            loadBundle(getExternalCacheDir()+"/finalbundle/"+getBundleFileName(bundleId));
                        }else {
                            showMsg("新功能正在开发中，敬请期待");
                        }
                    }
                }, throwable -> showMsg(throwable.getLocalizedMessage()));
    }

    private void testLocalUpdate() {
        String url = "http://10.181.12.38:8081/qrcode/upload/api/rnTestThreebundle.zip";
        if (bundleId == KEY_BUNDLE_ONE){
            url = "http://10.181.12.38:8081/qrcode/upload/api/rnbundleone.zip";
        }else if (bundleId == KEY_BUNDLE_TWO){
            url = "http://10.181.12.38:8081/qrcode/upload/api/rnbundletwo.zip";
        }
        downLoadBundle(url);
    }

    private String getBundleFileName(Integer bundleId) {
        if (bundleId == KEY_BUNDLE_ONE){
            return "rnbundleone";
        }else if (bundleId == KEY_BUNDLE_TWO){
            return "rnbundletwo";
        }else {
            return "finalbundle";
        }
    }

    private void showMsg(String msg){
        Toast.makeText(RNTest3Activity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void downLoadBundle(String bundleUrl) {
        RNBundleUtil.getInstance().downLoadBundle(bundleUrl, path -> loadBundle(path));
    }

    private void loadBundle(String path){
        File bundleFile = new File(path,bundleAssetName);
        String bundlePath = null;
        if(bundleFile.exists()){
            bundlePath = bundleFile.getAbsolutePath();
        }
        RNBridge.getInstance().setRootView(mReactRootView, moduleName, bundlePath, bundleAssetName, jsMainMoudlePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001){
            updateJsBundle();
        }
    }

}
