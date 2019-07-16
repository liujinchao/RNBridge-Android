package com.liujc.rnbridge.util.net;

import retrofit2.Retrofit;

/**
 * @ClassName:  IApiService
 * @author: liujc
 * @date: 2019/7/15
 * @Description: (这里用一句话描述这个类的作用)
 */
public interface IApiService {
    <T> T getApiService(final Class<T> service);
    Retrofit provideRetrofit();
}
