package com.liujc.rnbridge.util.net;

import android.support.annotation.NonNull;

import com.liujc.rnbridge.util.BundleVersionInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author liujc
 * @ClassName CommonService
 * @date 2019/7/15
 * @Description (这里用一句话描述这个类的作用)
 */
public interface CommonService {
    @GET("app/getBundleVersion")
    Observable<SingletonResponseEntity<BundleVersionInfo>> checkBundleVersion(@Query("bundleId") @NonNull Integer bundleId,
                                                                              @Query("appVersion") @NonNull String appVersion,
                                                                              @Query("sdkVersion") @NonNull String sdkVersion,
                                                                              @Query("bundleVersion") @NonNull String bundleVersion);

}
