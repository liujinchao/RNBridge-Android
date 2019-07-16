package com.liujc.rnbridge.util;

import java.io.Serializable;

/**
 * RN bundle 版本更新信息
 */
public class BundleVersionInfo implements Serializable {
    private static final long serialVersionUID = -7638919911187512074L;
    private String newVersion;
    private String bundleUrl;
    private String updateDesc;
    private boolean hasUpdate; // 有没有更新
    private boolean canUse; // 可不可用
    private String bundleSize;
    private String md5;

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getBundleUrl() {
        return bundleUrl;
    }

    public void setBundleUrl(String bundleUrl) {
        this.bundleUrl = bundleUrl;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public String getBundleSize() {
        return bundleSize;
    }

    public void setBundleSize(String bundleSize) {
        this.bundleSize = bundleSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "BundleVersionInfo{" +
                "newVersion='" + newVersion + '\'' +
                ", bundleUrl='" + bundleUrl + '\'' +
                ", updateDesc='" + updateDesc + '\'' +
                ", hasUpdate=" + hasUpdate +
                ", canUse=" + canUse +
                ", bundleSize='" + bundleSize + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
