package com.liujc.rnbridge.util.net;

import android.text.TextUtils;

/**
 * @ClassName:  NetHelper
 * @author: liujc
 * @date: 2019/7/15
 * @Description: 网络请求相关帮助类
 */
public class NetHelper {

    public static final String API_BASE_URL = "http://10.181.12.38:8087/";

    private volatile static IApiService iApiService;
    /**
     * 默认baseUrl和官网环境同步
     * @param apiClass
     * @param <T>
     * @return
     */
    public static  <T> T getApiService(Class<T> apiClass){
        return getApiService(API_BASE_URL, apiClass);
    }

    /**
     * @param baseUrl  api的baseUrl
     * @param apiClass 对应service类型
     * @return  所创建的对应service
     */
    public static  <T> T getApiService(String baseUrl, Class<T> apiClass) {
        if (TextUtils.isEmpty(baseUrl)){
            baseUrl = API_BASE_URL;
        }
        if (!isAreadyCreated(baseUrl)){
            iApiService = new RetrofitImpl(baseUrl);
        }
        return iApiService.getApiService(apiClass);
    }

    /**
     * 对应baseUrl的Retrofit是否已经创建
     * @param baseUrl
     * @return
     */
    private static boolean isAreadyCreated(String baseUrl) {
        return iApiService != null
                && iApiService.provideRetrofit() != null
                && iApiService.provideRetrofit().baseUrl() != null
                && iApiService.provideRetrofit().baseUrl().toString() != null
                && iApiService.provideRetrofit().baseUrl().toString().equals(baseUrl);
    }

}
