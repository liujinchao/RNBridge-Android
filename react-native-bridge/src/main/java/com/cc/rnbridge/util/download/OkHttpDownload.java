package com.cc.rnbridge.util.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.cc.rnbridge.util.RNBundleUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author liujc
 * @ClassName OkHttpDownload
 * @date 2019/7/31
 * @Description 基于okhttp下载zip文件处理类
 */
public class OkHttpDownload implements DownloadRequest{

    private static final int KEY_DOWNLOAD_PREPARE = 0;
    private static final int KEY_DOWNLOAD_SUCCESS = 1;
    private static final int KEY_DOWNLOAD_FAIL = 2;

    private String targetPath;

    private FileDownloadManagerListener listener;
    private Handler handler;
    private String fileName;

    @Override
    public void download(Context context, String url, final String targetPath, final String fileName, FileDownloadManagerListener listener) {
        this.targetPath = targetPath;
        this.listener = listener;
        this.fileName = fileName;
        initHandle(context);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendErrorMsg("下载错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File pathFile = new File(targetPath);
                    if (!pathFile.exists()){
                        pathFile.mkdirs();
                    }
                    File mBundleZipFile = new File(targetPath, fileName);
                    if (!mBundleZipFile.exists()){
                        mBundleZipFile.createNewFile();
                    }
                    fos = new FileOutputStream(mBundleZipFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                    }
                    if (fos != null){
                        fos.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMsg(e.getLocalizedMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {

                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {

                    }
                }
                // 下载完成,解压zip
                deleteOldData();
            }
        });
    }

    private void initHandle(Context context) {
        handler = new Handler(context.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case KEY_DOWNLOAD_PREPARE:
                        if (listener != null) {
                            listener.onPrepare();
                        }
                        break;
                    case KEY_DOWNLOAD_SUCCESS:
                        if (listener != null) {
                            listener.onSuccess(targetPath + "/" + getFileNameNoExt(fileName));
                        }
                        break;
                    case KEY_DOWNLOAD_FAIL:
                        if (listener != null) {
                            listener.onFailed(new Throwable(msg.obj.toString()));
                        }
                        break;
                }
            }
        };
        handler.sendEmptyMessage(KEY_DOWNLOAD_PREPARE);
    }

    private void sendErrorMsg(String errMsg) {
        Message message = new Message();
        message.what = KEY_DOWNLOAD_FAIL;
        message.obj = errMsg;
        handler.sendMessage(message);
    }

    private void deleteOldData() {
        File reactbundleDir = new File(targetPath, getFileNameNoExt(fileName));
        RNBundleUtil.deleteDir(reactbundleDir);
        replaceBundle();
    }

    protected void replaceBundle() {
        File mBundleZipFile =  new File(targetPath, fileName);
        if (mBundleZipFile == null || !mBundleZipFile.exists()){
            sendErrorMsg("下载失败");
            return;
        }
        boolean result = RNBundleUtil.unzip(mBundleZipFile);

        if(result){//解压成功后保存当前最新bundle的版本
            handler.sendEmptyMessage(KEY_DOWNLOAD_SUCCESS);
        }else{//解压失败应该删除掉有问题的文件，防止RN加载错误的bundle文件
            sendErrorMsg("解压失败");
            File reactbundleDir = new File(targetPath, getFileNameNoExt(fileName));
            RNBundleUtil.deleteDir(reactbundleDir);
        }
    }

    /**
     * 获取解压文件夹名称
     * @param name
     * @return
     */
    private static String getFileNameNoExt(String name) {
        if (!TextUtils.isEmpty(name) && name.contains(".zip")){
            return name.substring(0, name.lastIndexOf(".zip"));
        }
        return name;
    }

}
