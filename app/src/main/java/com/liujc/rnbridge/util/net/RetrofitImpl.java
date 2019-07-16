package com.liujc.rnbridge.util.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ClassName:  RetrofitImpl
 * @author: liujc
 * @date: 2019/7/15
 * @Description: 创建Retrofit
 */
public class RetrofitImpl implements IApiService {
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    //读超时长，单位：秒
    public static final int READ_TIME_OUT = 30;
    public static final int WRITE_TIME_OUT = 30;
    //连接时长，单位：秒
    public static final int CONNECT_TIME_OUT = 30;

    public RetrofitImpl(String baseUrl) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();
        mRetrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Override
    public <T> T getApiService(Class<T> service) {
        return mRetrofit.create(service);
    }

    @Override
    public Retrofit provideRetrofit() {
        return mRetrofit;
    }

    private OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitImpl.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = initOkHttpClient();
                }
            }
        }
        return mOkHttpClient;
    }
    private OkHttpClient initOkHttpClient(InputStream... certificates) {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .build();
        return mOkHttpClient;
    }
}
