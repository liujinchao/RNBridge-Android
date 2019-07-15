package com.cc.rnbridge.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;
import java.util.List;

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
        return toArrayList(readableArray).toArray();
    }

    public static WritableArray toWritableArray(ArrayList arrayList) {
        return Arguments.fromList(arrayList);
    }

    public static ArrayList toArrayList(ReadableArray readableArray) {
        return Arguments.toList(readableArray);
    }
    public static WritableArray toWritableArray(Object[] args) {
        return Arguments.fromJavaArgs(args);
    }
    public static WritableArray toWritableArray(Object array) {
        return Arguments.fromArray(array);
    }

    public static WritableArray toWritableArray(List objects) {
        return Arguments.makeNativeArray(objects);
    }
}
