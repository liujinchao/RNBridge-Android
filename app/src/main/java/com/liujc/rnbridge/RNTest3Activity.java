package com.liujc.rnbridge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.util.RNBundleUtil;
import com.facebook.react.ReactRootView;

import java.io.File;

/**
 * @author liujc
 * @ClassName RN2Activity
 * @date 2019/7/13
 * @Description (这里用一句话描述这个类的作用)
 */
public class RNTest3Activity extends AppCompatActivity {
    private ReactRootView mReactRootView;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, ReactRootView.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rn3_layout);
        initView();
    }

    private void initView() {
        mReactRootView = findViewById(R.id.rrv_rn);
//        RNBridge.getInstance().setRootView(mReactRootView, "rnTest3","index.androidtestT.bundle");
//        RNBridge.getInstance().setRootView(mReactRootView, "rnTest3","rnTestThree.bundle","rnTestThree");

//        loadBundle();
        updateJsBundle();
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
        if(BuildConfig.BUNDLE_VERSION == "1.0.1"){//TODO:这里需要发起异步获取服务端的版本号，然后和打包版本号比对
            if (!RNBundleUtil.checkPermission(RNTest3Activity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001)){
                return;
            }
            RNBundleUtil.getInstance().downLoadBundle("http://10.181.12.38:8081/qrcode/upload/api/rnTestThreebundle.zip", new RNBundleUtil.UpdateBundleCallBack() {
                @Override
                public void getUnZipBundlePath(String path) {
                    loadBundle(path);
                }
            });
        }else {
            loadBundle();
        }
    }

    private void loadBundle(String path){
        File bundleFile = new File(path,"rnTestThree.bundle");
        String bundlePath = null;
        if(bundleFile.exists()){
            bundlePath = bundleFile.getAbsolutePath();
        }
        RNBridge.getInstance().setRootView(mReactRootView, "rnTest3", bundlePath,"rnTestThree.bundle","rnTestThree");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001){
            updateJsBundle();
        }
    }

}
