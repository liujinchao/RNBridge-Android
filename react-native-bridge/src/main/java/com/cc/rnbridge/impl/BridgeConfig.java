package com.cc.rnbridge.impl;

import com.facebook.react.ReactPackage;

import java.util.List;

/**
 * @author liujc
 * @ClassName BridgeConfig
 * @date 2019/7/18
 * @Description 需要初始化的配置信息
 */
public interface BridgeConfig {

    /**
     * RN需要的package
     * @return
     */
    List<ReactPackage> getReactPackages();

    boolean isDebug();

}
