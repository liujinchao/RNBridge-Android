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
    }

    private void initData() {
        toRN = findViewById(R.id.toRN);
        toRN.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RNActivity.class)));

        toRN2 = findViewById(R.id.toRN2);
        toRN2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RN2Activity.class)));

        toRNTest3 = findViewById(R.id.toRNTest3);
        toRNTest3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RNTest3Activity.class)));

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
