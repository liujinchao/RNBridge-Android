package com.cc.rnbridge.util;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.Set;

/**
 * @author liujc
 * @ClassName UrlUtil
 * @date 2019/7/23
 * @Description Url解析工具类
 */
public class UrlUtil {
    /**
     * 转化成scheme://host/path?key=value形式
     * @param url
     * @param paramsMp
     * @return
     */
    public static String toUrlParams(String url, Bundle paramsMp){
        if (TextUtils.isEmpty(url)
                || paramsMp == null
                || paramsMp.size() == 0){
            return url;
        }
        StringBuilder routeUrl = new StringBuilder(url);
        StringBuilder params = new StringBuilder();
        String paramStr = null;

        Set<String> keySet = paramsMp.keySet();
        for(String key : keySet) {
            Object value = paramsMp.get(key);
            params.append("&"+key+"="+value);
        }
        if (!TextUtils.isEmpty(params.toString())){
            paramStr = params.deleteCharAt(0).toString();
        }

        if (!routeUrl.toString().contains("?")){
            routeUrl.append("?");
        }
        if (!TextUtils.isEmpty(paramStr)){
            routeUrl.append(paramStr);
        }
        return routeUrl.toString();
    }

    /**
     * 解析url中的params部分添加到已有的bundle中
     * @param url
     * @return
     */
    public static UrlEntity toUrlBundle(String url, Bundle tmpParams) {
        UrlEntity entity = new UrlEntity();
        if (tmpParams == null){
            tmpParams = new Bundle();
        }
        entity.params = tmpParams;

        if (TextUtils.isEmpty(url)) {
            return entity;
        }
        url = url.trim();
        if (TextUtils.isEmpty(url)) {
            return entity;
        }

        String[] urlParts = url.split("\\?");
        entity.baseUrl = urlParts[0];
        //没有参数params部分
        if (urlParts.length == 1) {
            return entity;
        }
        //有参数params部分
        String[] params = urlParts[1].split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            String key = keyValue[0];
            Object value = keyValue[1];
            if (value == null) {
                entity.params.putString(keyValue[0], null);
            } else if (value instanceof Boolean) {
                entity.params.putBoolean(keyValue[0], (Boolean) value);
            } else if (value instanceof Integer) {
                entity.params.putInt(key, (Integer) value);
            } else if (value instanceof Number) {
                entity.params.putDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof String) {
                entity.params.putString(key, (String) value);
            }
        }
        return entity;
    }

    public static class UrlEntity {
        /**
         * 基础url
         */
        public String baseUrl;
        /**
         * url参数
         */
        public Bundle params;
    }

}
