package com.cc.rnbridge.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;
import java.util.Map;

/**
 * @ClassName:  RNArrayUtil
 * @author: liujc
 * @date: 2019/6/5
 * @Description: 原生与RN中的ReadableArray工具类
 */
public class RNArrayUtil {

    /**
     * RN中数组ReadableArray转JSONArray
     * @param readableArray
     * @return
     * @throws JSONException
     */
    public static JSONArray toJSONArray(ReadableArray readableArray) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < readableArray.size(); i++) {
            ReadableType type = readableArray.getType(i);

            switch (type) {
                case Null:
                    jsonArray.add(i, null);
                    break;
                case Boolean:
                    jsonArray.add(i, readableArray.getBoolean(i));
                    break;
                case Number:
                    jsonArray.add(i, readableArray.getDouble(i));
                    break;
                case String:
                    jsonArray.add(i, readableArray.getString(i));
                    break;
                case Map:
                    jsonArray.add(i, RNMapUtil.toJSONObject(readableArray.getMap(i)));
                    break;
                case Array:
                    jsonArray.add(i, RNArrayUtil.toJSONArray(readableArray.getArray(i)));
                    break;
            }
        }

        return jsonArray;
    }

    /**
     * JSONArray 转 Object[]
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    public static Object[] toArray(JSONArray jsonArray) throws JSONException {
        Object[] array = new Object[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            Object value = jsonArray.get(i);

            if (value instanceof JSONObject) {
                value = RNMapUtil.toMap((JSONObject) value);
            }
            if (value instanceof JSONArray) {
                value = RNArrayUtil.toArray((JSONArray) value);
            }

            array[i] = value;
        }

        return array;
    }

    /**
     * ReadableArray 转 Object[]
     * @param readableArray
     * @return
     */
    public static Object[] toArray(ReadableArray readableArray) {
        Object[] array = new Object[readableArray.size()];
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableType type = readableArray.getType(i);

            switch (type) {
                case Null:
                    array[i] = null;
                    break;
                case Boolean:
                    array[i] = readableArray.getBoolean(i);
                    break;
                case Number:
                    array[i] = readableArray.getDouble(i);
                    break;
                case String:
                    array[i] = readableArray.getString(i);
                    break;
                case Map:
                    array[i] = RNMapUtil.toMap(readableArray.getMap(i));
                    break;
                case Array:
                    array[i] = RNArrayUtil.toArray(readableArray.getArray(i));
                    break;
            }
        }

        return array;
    }

    /**
     * Object[] 转 WritableArray
     * @param array
     * @return
     */
    public static WritableArray toWritableArray(Object[] array) {
        WritableArray writableArray = Arguments.createArray();

        for (int i = 0; i < array.length; i++) {
            Object value = array[i];

            if (value == null) {
                writableArray.pushNull();
            }
            if (value instanceof Boolean) {
                writableArray.pushBoolean((Boolean) value);
            }
            if (value instanceof Double) {
                writableArray.pushDouble((Double) value);
            }
            if (value instanceof Integer) {
                writableArray.pushInt((Integer) value);
            }
            if (value instanceof String) {
                writableArray.pushString((String) value);
            }
            if (value instanceof Map) {
                writableArray.pushMap(RNMapUtil.toWritableMap((Map<String, Object>) value));
            }
            if (value.getClass().isArray()) {
                writableArray.pushArray(RNArrayUtil.toWritableArray((Object[]) value));
            }
        }

        return writableArray;
    }

    public static WritableArray toWritableArray(ArrayList arrayList) {
        if (arrayList != null && arrayList.size() > 0){
            return toWritableArray(arrayList.toArray());
        }
        return Arguments.createArray();
    }

}
