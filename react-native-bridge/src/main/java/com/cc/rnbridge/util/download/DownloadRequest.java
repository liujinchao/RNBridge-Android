package com.cc.rnbridge.util.download;

import android.content.Context;

/**
 * @author liujc
 * @ClassName DownloadRequest
 * @date 2019/7/31
 * @Description 下载请求类
 */
public interface DownloadRequest {

    void download(Context context, String url, String targetPath, String fileName, FileDownloadManagerListener listener);

}
