package com.liujc.rnbridge;

import android.app.Application;

import com.cc.rnbridge.RNBridge;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return RNBridge.getInstance().getReactPackages();
    }

  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    RNBridge.getInstance().initRNConfig(this,
            BuildConfig.DEBUG, Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new TestReactPackage()
            ));
    SoLoader.init(this, /* native exopackage */ false);
  }
}
