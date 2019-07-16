package com.liujc.rnbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.entity.Event;
import com.cc.rnbridge.util.RNEventEmitter;
import com.cc.rnbridge.util.RNMapUtil;
import com.facebook.react.ReactRootView;
import com.liujc.rnbridge.rn.TestEvent;
import com.liujc.rnbridge.util.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ReactRootView mReactRootView;
    private TextView toRN;
    private TextView toRN2;
    private TextView toRNTest3;
    private TextView toRNSetting;
    private TextView loadRN;
    private TextView sendMsgToRN;
    private TextView sendMapToRN;
    private TextView sendListToRN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initView();
        initData();
        initTestBundle();
    }

    private void initTestBundle() {
        TextView toRNTestOne = findViewById(R.id.toRNTestOne1);
        toRNTestOne.setOnClickListener(v -> {
            SpUtil.setBundleVersion(MainActivity.this,RNTest3Activity.KEY_BUNDLE_ONE, "1.0.1");
            RNTest3Activity.startActivity(MainActivity.this, RNTest3Activity.KEY_BUNDLE_ONE,"1.0.1");
        });
        TextView toRNTestOne2 = findViewById(R.id.toRNTestOne2);
        toRNTestOne2.setOnClickListener(v -> {
            SpUtil.setBundleVersion(MainActivity.this,RNTest3Activity.KEY_BUNDLE_ONE, "1.0.2");
            RNTest3Activity.startActivity(MainActivity.this, RNTest3Activity.KEY_BUNDLE_ONE,"1.0.2");
        });
        TextView toRNTestOne3 = findViewById(R.id.toRNTestOne3);
        toRNTestOne3.setOnClickListener(v -> {
            SpUtil.setBundleVersion(MainActivity.this,RNTest3Activity.KEY_BUNDLE_ONE, "1.0.3");
            RNTest3Activity.startActivity(MainActivity.this, RNTest3Activity.KEY_BUNDLE_ONE,"1.0.3");
        });
        TextView toRNTestTwo = findViewById(R.id.toRNTestTwo);
        toRNTestTwo.setOnClickListener(v -> {
            RNTest3Activity.startActivity(MainActivity.this, RNTest3Activity.KEY_BUNDLE_TWO, BuildConfig.BUNDLE_TWO_VERSION);
        });
        TextView toRNTestTwo2 = findViewById(R.id.toRNTestTwo2);
        toRNTestTwo2.setOnClickListener(v -> {
            SpUtil.setBundleVersion(MainActivity.this,RNTest3Activity.KEY_BUNDLE_TWO, "1.0.2");
            RNTest3Activity.startActivity(MainActivity.this, RNTest3Activity.KEY_BUNDLE_TWO,"1.0.2");
        });
        TextView toRNTestTwo3 = findViewById(R.id.toRNTestTwo3);
        toRNTestTwo3.setOnClickListener(v -> {
            SpUtil.setBundleVersion(MainActivity.this,RNTest3Activity.KEY_BUNDLE_TWO, "1.0.3");
            RNTest3Activity.startActivity(MainActivity.this, RNTest3Activity.KEY_BUNDLE_TWO,"1.0.3");
        });
        TextView toRNTestThree = findViewById(R.id.toRNTestThree);
        toRNTestThree.setOnClickListener(v -> {
            RNTest3Activity.startActivity(MainActivity.this);
        });
    }

    private void initData() {
        toRN = findViewById(R.id.toRN);
        toRN.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RNActivity.class)));

        toRN2 = findViewById(R.id.toRN2);
        toRN2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RN2Activity.class)));

        toRNTest3 = findViewById(R.id.toRNTest3);
        toRNTest3.setOnClickListener(v -> RNTest3Activity.startActivity(MainActivity.this));

        toRNSetting = findViewById(R.id.toRNSetting);
        toRNSetting.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, com.facebook.react.devsupport.DevSettingsActivity.class)));

        loadRN = findViewById(R.id.loadRN);
        loadRN.setOnClickListener(v -> RNBridge.getInstance().setRootView(mReactRootView, "rn_test"));

        sendMsgToRN = findViewById(R.id.sendmsg);
        sendMsgToRN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestEvent testEvent = new TestEvent();
                testEvent.setName("小龙女");
                testEvent.setAge(18);
                RNEventEmitter.sendEvent(RNEventEmitter.DEFAULT_EVENT_NAME, RNMapUtil.toWritableMap(testEvent));

                Event<TestEvent> eventEvent = new Event<>();
                eventEvent.setCode(200);
                eventEvent.setMsg("native send 对象(beautiful girl) to RN2Activity");
                eventEvent.setData(testEvent);
                RNEventEmitter.sendEvent(eventEvent);
            }
        });
        sendListToRN = findViewById(R.id.sendList);
        sendListToRN.setOnClickListener(v -> {
            List<String> list = new ArrayList<>();
            list.add("a");
            list.add("b");
            RNEventEmitter.sendEvent(RNEventEmitter.DEFAULT_EVENT_NAME,list.toArray());

            Event<List> eventEvent = new Event<>();
            eventEvent.setCode(200);
            eventEvent.setMsg("native send list to RN2Activity");
            eventEvent.setData(list);
            RNEventEmitter.sendEvent(eventEvent);

        });

        sendMapToRN = findViewById(R.id.sendmap);
        sendMapToRN.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("map","this is map");
            RNEventEmitter.sendEvent(RNEventEmitter.DEFAULT_EVENT_NAME,map);

            Event<Map> eventEvent = new Event<>();
            eventEvent.setCode(200);
            eventEvent.setMsg("native send map to RN2Activity");
            eventEvent.setData(map);
            RNEventEmitter.sendEvent(eventEvent);
        });

//        RNBridge.getInstance().setRootView(mReactRootView, "rn_test");
    }

    private void initView() {
        mReactRootView = findViewById(R.id.rrv_rn);
    }
}
