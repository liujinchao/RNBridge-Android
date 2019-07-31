package com.cc.rnbridge.util.download;

import android.content.Context;
import android.os.Environment;

/**
 * @author liujc
 * @ClassName FileDownloadManager
 * @date 2019/7/29 文件下载工具类
 */
public class FileDownloadManager {

    private final String KEY_BUNDLE_DEFAULT_PATH_FILE = "finalbundle"; //bundle.zip文件保存的默认文件夹名称

    private Context context;
    private String url;
    private String name;
    private DownloadRequest downloadRequest;
    /**
     * 下载文件的根目录
     */
    private String targetPath;

    private FileDownloadManagerListener listener;

    public FileDownloadManager(Context context, String url) {
        this(context, url, getFileNameByUrl(url));
    }

    public FileDownloadManager(Context context, String url, String name) {
        this.context = context;
        this.url = url;
        this.name = name;
        targetPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+KEY_BUNDLE_DEFAULT_PATH_FILE;
    }

    public FileDownloadManager setListener(FileDownloadManagerListener listener) {
        this.listener = listener;
        return this;
    }

    public FileDownloadManager setDownloadRequest(DownloadRequest downloadRequest) {
        this.downloadRequest = downloadRequest;
        return this;
    }

    /**
     * 开始下载
     */
    public void download() {
        if (downloadRequest != null){
            downloadRequest.download(context, url, targetPath, name, listener);
        }
    }

    /**
     * 通过URL获取文件名
     *
     * @param url
     * @return
     */
    private static final String getFileNameByUrl(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        filename = filename.substring(0, filename.indexOf("?") == -1 ? filename.length() : filename.indexOf("?"));
        return filename;
    }

}
