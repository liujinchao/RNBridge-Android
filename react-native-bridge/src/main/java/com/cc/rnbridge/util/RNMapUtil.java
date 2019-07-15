package com.cc.rnbridge.util;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName:  RNMapUtil
 * @author: liujc
 * @date: 2019/6/5
 * @Description: 原生与RN中的ReadableMap工具类
 */
public class RNMapUtil {

    /**
     * ReadableMap 转 JSONObject
     * @param readableMap
     * @return
     * @throws JSONException
     */
    public static JSONObject toJSONObject(ReadableMap readableMap) throws JSONException{
        JSONObject jsonObject = new JSONObject();

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = readableMap.getType(key);

            switch (type) {
                case Null:
                    jsonObject.put(key, null);
                    break;
                case Boolean:
                    jsonObject.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    jsonObject.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    jsonObject.put(key, readableMap.getString(key));
                    break;
                case Map:
                    jsonObject.put(key, toJSONObject(readableMap.getMap(key)));
                    break;
                case Array:
                    jsonObject.put(key, RNArrayUtil.toJSONArray(readableMap.getArray(key)));
                    break;
            }
        }

        return jsonObject;
    }

    /**
     * JSONObject 转 Map<String, Object>
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            if (value instanceof JSONArray) {
                value = RNArrayUtil.toArray((JSONArray) value);
            }

            map.put(key, value);
        }

        return map;
    }

    public static <T> WritableMap toWritableMap(Object object){
        if (object != null){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
            return toWritableMap(toMap(jsonObject));
        }
        return Arguments.createMap();
    }

    public static WritableMap toWritableMap(Bundle bundle) {
        return Arguments.fromBundle(bundle);
    }

    public static WritableMap toWritableMap(Map<String, Object> objects) {
        return Arguments.makeNativeMap(objects);
    }

    public static Bundle toBundle(ReadableMap writableMap) {
        return Arguments.toBundle(writableMap);
    }

    public static Map<String, Object> toMap(ReadableMap writableMap) {
        return writableMap.toHashMap();
    }

}
