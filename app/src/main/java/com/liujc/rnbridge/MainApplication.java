package com.liujc.rnbridge;

import android.app.Application;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.impl.BridgeConfig;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.liujc.rnbridge.rn.TestReactPackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  @Override
  public ReactNativeHost getReactNativeHost() {
    return RNBridge.getInstance().getReactNativeHost(this);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    initRNConfig();
  }

  private void initRNConfig() {
    RNBridge.getInstance().initRNConfig(this, new BridgeConfig() {

      @Override
      public List<ReactPackage> getReactPackages() {
        return Arrays.<ReactPackage>asList(new MainReactPackage(),
                new TestReactPackage());
      }

      @Override
      public boolean isDebug() {
        return BuildConfig.DEBUG;
      }

    });
    SoLoader.init(this, /* native exopackage */ false);

  }
}
