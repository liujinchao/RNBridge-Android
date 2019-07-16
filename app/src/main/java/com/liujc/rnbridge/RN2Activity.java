package com.liujc.rnbridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cc.rnbridge.RNBridge;
import com.facebook.react.ReactRootView;

/**
 * @author liujc
 * @ClassName RN2Activity
 * @date 2019/7/13
 * @Description (这里用一句话描述这个类的作用)
 */
public class RN2Activity extends AppCompatActivity {
    private ReactRootView mReactRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rn2_layout);
        initView();
    }

    private void initView() {
        mReactRootView = findViewById(R.id.rrv_rn);
        RNBridge.getInstance().setRootView(mReactRootView, "rnTest2","index.androidimg.bundle");
    }
}