package com.cc.rnbridge.entity;

import android.text.TextUtils;

import java.io.File;

/**
 * @author liujc
 * @ClassName BundleConfig
 * @date 2019/7/16
 * @Description RN Bundle 配置信息
 */
public class BundleConfig {

    private String moduleName;
    private String bundleFile;
    private String bundleAssetName;
    private String jsMainMoudlePath;

    public BundleConfig() {
    }

    private BundleConfig(BundleConfigBuild builder) {
       this.moduleName = builder.moduleName;
       this.bundleFile = builder.bundlePath;
       this.bundleAssetName = builder.bundleAssetName;
       this.jsMainMoudlePath = builder.jsMainMoudlePath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getBundleFile() {
        if (TextUtils.isEmpty(bundleFile)){
            return null;
        }
        File localBundleFile = new File(bundleFile);
        if(localBundleFile.exists()){
            return localBundleFile.getAbsolutePath();
        }
        return null;
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

    public static class BundleConfigBuild {
        private String moduleName;
        private String bundlePath;
        private String bundleAssetName;
        private String jsMainMoudlePath;

        public BundleConfig build() {
            return new BundleConfig(this);
        }

        public String getModuleName() {
            return moduleName;
        }

        public BundleConfigBuild setModuleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public String getBundlePath() {
            return bundlePath;
        }

        public BundleConfigBuild setBundlePath(String bundleFile) {
            this.bundlePath = bundleFile;
            return this;
        }

        public String getBundleAssetName() {
            return bundleAssetName;
        }

        public BundleConfigBuild setBundleAssetName(String bundleAssetName) {
            this.bundleAssetName = bundleAssetName;
            return this;
        }

        public String getJsMainMoudlePath() {
            return jsMainMoudlePath;
        }

        public BundleConfigBuild setJsMainMoudlePath(String jsMainMoudlePath) {
            this.jsMainMoudlePath = jsMainMoudlePath;
            return this;
        }
    }
}
