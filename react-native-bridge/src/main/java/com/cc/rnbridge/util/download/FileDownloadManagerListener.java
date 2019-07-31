package com.cc.rnbridge.util.download;

/**
 * @author liujc
 * @ClassName FileDownloadManagerListener
 * @date 2019/7/29
 * @Description 下载管理监听
 */
public interface FileDownloadManagerListener {

    void onPrepare();

    void onSuccess(String path);

    void onFailed(Throwable throwable);

}
