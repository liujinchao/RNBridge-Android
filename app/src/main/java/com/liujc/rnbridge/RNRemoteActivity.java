package com.liujc.rnbridge;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
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
 * @author liujc
 * @ClassName RNRemoteActivity
 * @date 2019/7/13
 * @Description 测试加载远程bundle
 */
public class RNRemoteActivity extends AppCompatActivity {

    public static final int KEY_BUNDLE_ONE = 1001;
    public static final int KEY_BUNDLE_TWO = 1002;

    /**
     * 测试加载assets中的bundle文件 start
     */
    public static void loadRNTest3(Context context){
        loadLocalBundle(context, 1003,"rnTest3", null, null );
    }

    public static void loadLocalBundleTestOne(Context context){
        loadLocalBundle(context, 1004,"rnTest1", "rnbundleone.bundle", null);
    }
    public static void loadLocalBundleTestTwo(Context context){
        loadLocalBundle(context, 1005,"rnTest2", "rnbundletwo.bundle", null);
    }

    public static void loadLocalBundle(Context context,
                                       Integer bundleId,
                                       String moduleName,
                                       String bundleAssetName,
                                       String jsMainMoudlePat){
        Bundle bundle = new Bundle();
        bundle.putString("test", moduleName + "  " + bundleAssetName + "  " + jsMainMoudlePat);
        BundleConfig bundleConfig = new BundleConfig.BundleConfigBuild()
                .setBundleId(bundleId)
                .setModuleName(moduleName)
                .setBundleAssetName(bundleAssetName)
                .setJsMainMoudlePath(jsMainMoudlePat)
                .setAppProperties(bundle)
                .build();
        openRnUrl(context, ReactBridgeActivity.DEFAULT_RN_HOST,bundleConfig);
    }
    /**
     * 测试加载assets中的bundle文件 end
     */

    public static void loadRemote(Context context){
        Bundle bundle = new Bundle();
        bundle.putString("test", "RNRemoteActivity");
        BundleConfig bundleConfig = new BundleConfig.BundleConfigBuild()
                .setModuleName("rnTest3")
                .setAppProperties(bundle)
                .build();
        openRnUrl(context, ReactBridgeActivity.DEFAULT_RN_HOST ,bundleConfig);
    }

    private BundleConfig mBundleConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_bundle_layout);
        initTestRemoteBundle();
    }
    private void initTestRemoteBundle() {
        TextView toRNTestOne = findViewById(R.id.toRNTestOne1);
        toRNTestOne.setOnClickListener(v -> {
//            mBundleConfig  = loadRemoteBundle(KEY_BUNDLE_ONE,"1.0.1");
//            if (mBundleConfig != null){
//               updateJsBundle();
//            }
            BundleUpdateActivity.openRNBundle(RNRemoteActivity.this, "/testOne?age=18&bundleId="+ BundleUpdateActivity.KEY_BUNDLE_ONE, null);
        });

        TextView toRNTestTwo = findViewById(R.id.toRNTestTwo);
        toRNTestTwo.setOnClickListener(v -> {
//            mBundleConfig = loadRemoteBundle(KEY_BUNDLE_TWO, BuildConfig.BUNDLE_TWO_VERSION);
//            if (mBundleConfig != null){
//                updateJsBundle();
//            }
            BundleUpdateActivity.openRNBundle(RNRemoteActivity.this, "/testTwo?age=20&bundleId="+ BundleUpdateActivity.KEY_BUNDLE_TWO, null);

        });

        TextView toRNTestTwo2 = findViewById(R.id.toRNTestTwo2);
        toRNTestTwo2.setOnClickListener(v -> {
            mBundleConfig = loadRemoteBundle(KEY_BUNDLE_TWO,"1.0.2");
            if (mBundleConfig != null){
                updateJsBundle();
            }
        });
        TextView toRNTestTwo3 = findViewById(R.id.toRNTestTwo3);
        toRNTestTwo3.setOnClickListener(v -> {
            mBundleConfig = loadRemoteBundle(KEY_BUNDLE_TWO,"1.0.3");
            if (mBundleConfig != null){
                updateJsBundle();
            }
        });

    }

    /**
     * 模拟加载两个bundle，然后进行版本检查
     * @param bundleId
     * @param bundleVersion
     * @return
     */
    public BundleConfig loadRemoteBundle(Integer bundleId, String bundleVersion){
        Bundle bundle = new Bundle();
        bundle.putInt("bundleId",bundleId);
        bundle.putString("bundleVersion", bundleVersion);
        if (bundleId == KEY_BUNDLE_ONE){
            BundleConfig bundleConfig = new BundleConfig.BundleConfigBuild()
                    .setBundleId(bundleId)
                    .setBundleVersion(bundleVersion)
                    .setModuleName("rnTest1")
                    .setBundleAssetName("rnbundleone.bundle")
                    .setJsMainMoudlePath("rnTestOne")
                    .setAppProperties(bundle)
                    .build();
            return bundleConfig;
        }else if (bundleId == KEY_BUNDLE_TWO){
            BundleConfig bundleConfig = new BundleConfig.BundleConfigBuild()
                    .setBundleId(bundleId)
                    .setBundleVersion(bundleVersion)
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
        if (!RNBundleUtil.checkPermission(RNRemoteActivity.this, new String[]{
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
                            }
                        }else if (bundleInfo.isCanUse()){
                            showMsg("当前已是最新版本");
                            loadBundle(getExternalCacheDir()+"/finalbundle/"+getBundleFileName(mBundleConfig.getBundleId()));
                        }else {
                            showMsg("新功能正在开发中，敬请期待");
                        }
                    }
                }, throwable -> showMsg(throwable.getLocalizedMessage()));
    }

    /**
     * 不走版本检查接口，直接根据链接下载然后加载
     */
    private void testLocalUpdate() {
        String url = "http://10.181.12.38:8081/qrcode/upload/api/rnTestThreebundle.zip";
        if (mBundleConfig.getBundleId() == KEY_BUNDLE_ONE){
            url = "http://10.181.12.38:8081/qrcode/upload/api/rnbundleone.zip";
        }else if (mBundleConfig.getBundleId() == KEY_BUNDLE_TWO){
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
        Toast.makeText(RNRemoteActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void downLoadBundle(String bundleUrl) {
        RNBundleUtil.getInstance().downLoadBundle(bundleUrl, path -> loadBundle(path));
    }

    private void loadBundle(String path){
        File bundleFile = new File(path,mBundleConfig.getBundleAssetName());
        String bundlePath = null;
        if(bundleFile.exists()){
            bundlePath = bundleFile.getAbsolutePath();
        }
        mBundleConfig.setBundleFilePath(bundlePath);
        openRnUrl(RNRemoteActivity.this, ReactBridgeActivity.DEFAULT_RN_HOST ,mBundleConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001){
            updateJsBundle();
        }
    }
}
