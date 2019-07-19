package com.cc.rnbridge.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

/**
 * @author liujc
 * @ClassName BundleConfig
 * @date 2019/7/16
 * @Description RN Bundle 配置信息
 */
public class BundleConfig implements Parcelable{

    private Integer bundleId;           // bundle 文件唯一标示
    private String moduleName;          // 注册在js中的名称
    private String bundleVersion;       // bundle 文件对应的版本号，检查bundle更新时使用
    private String bundleFilePath;      // bundle文件的路径
    private String bundleAssetName;     // bundle文件名称
    private String jsMainMoudlePath;    // bundle文件的启动入口js文件
    private Bundle appProperties;       // 需要携带给RN页面的参数

    public BundleConfig() {
    }

    private BundleConfig(BundleConfigBuild builder) {
       this.bundleId = builder.bundleId;
       this.moduleName = builder.moduleName;
       this.bundleFilePath = builder.bundleFilePath;
       this.bundleAssetName = builder.bundleAssetName;
       this.jsMainMoudlePath = builder.jsMainMoudlePath;
       this.appProperties = builder.appProperties;
       this.bundleVersion = builder.bundleVersion;
    }

    protected BundleConfig(Parcel in) {
        if (in.readByte() == 0) {
            bundleId = null;
        } else {
            bundleId = in.readInt();
        }
        moduleName = in.readString();
        bundleFilePath = in.readString();
        bundleAssetName = in.readString();
        jsMainMoudlePath = in.readString();
        appProperties = in.readBundle();
        bundleVersion = in.readString();
    }

    public static final Creator<BundleConfig> CREATOR = new Creator<BundleConfig>() {
        @Override
        public BundleConfig createFromParcel(Parcel in) {
            return new BundleConfig(in);
        }

        @Override
        public BundleConfig[] newArray(int size) {
            return new BundleConfig[size];
        }
    };

    public String getModuleName() {
        return moduleName;
    }

    public String getBundleFilePath() {
        if (TextUtils.isEmpty(bundleFilePath)){
            return null;
        }
        File localBundleFile = new File(bundleFilePath);
        if(localBundleFile.exists()){
            return localBundleFile.getAbsolutePath();
        }
        return null;
    }

    public void setBundleFilePath(String bundleFilePath) {
        this.bundleFilePath = bundleFilePath;
    }

    public String getBundleAssetName() {
        if (!TextUtils.isEmpty(bundleAssetName)){
            return bundleAssetName;
        }
        return "index.android.bundle";
    }

    public String getJsMainMoudlePath() {
        if (!TextUtils.isEmpty(jsMainMoudlePath)){
            return jsMainMoudlePath;
        }
        return "index.android";
    }

    public int getBundleId() {
        return bundleId;
    }

    public Bundle getAppProperties() {
        return appProperties;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (bundleId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(bundleId);
        }
        dest.writeString(moduleName);
        dest.writeString(bundleFilePath);
        dest.writeString(bundleAssetName);
        dest.writeString(jsMainMoudlePath);
        dest.writeBundle(appProperties);
        dest.writeString(bundleVersion);
    }

    public static class BundleConfigBuild {
        private int bundleId;
        private String moduleName;
        private String bundleFilePath;
        private String bundleAssetName;
        private String jsMainMoudlePath;
        private Bundle appProperties;
        private String bundleVersion;

        public BundleConfig build() {
            return new BundleConfig(this);
        }

        public BundleConfigBuild setBundleId(int bundleId) {
            this.bundleId = bundleId;
            return this;
        }

        public BundleConfigBuild setAppProperties(Bundle appProperties) {
            this.appProperties = appProperties;
            return this;
        }

        public BundleConfigBuild setModuleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public BundleConfigBuild setBundlePath(String bundleFilePath) {
            this.bundleFilePath = bundleFilePath;
            return this;
        }

        public BundleConfigBuild setBundleAssetName(String bundleAssetName) {
            this.bundleAssetName = bundleAssetName;
            return this;
        }

        public BundleConfigBuild setJsMainMoudlePath(String jsMainMoudlePath) {
            this.jsMainMoudlePath = jsMainMoudlePath;
            return this;
        }

        public BundleConfigBuild setBundleVersion(String bundleVersion) {
            this.bundleVersion = bundleVersion;
            return this;
        }
    }
}
