package com.cc.rnbridge.util;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.cc.rnbridge.RNBridge;
import com.cc.rnbridge.util.download.DownloadRequest;
import com.cc.rnbridge.util.download.FileDownloadManager;
import com.cc.rnbridge.util.download.FileDownloadManagerListener;
import com.cc.rnbridge.util.download.OkHttpDownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * @author liujc
 * @ClassName RNBundleUtil
 * @date 2019/7/14
 * @Description jsbundle 相关处理工具
 */
public class RNBundleUtil {

    private static volatile RNBundleUtil defaultInstance;

    private RNBundleUtil() {
    }

    public static RNBundleUtil getInstance() {
        if (defaultInstance == null) {
            synchronized (RNBundleUtil.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RNBundleUtil();
                }
            }
        }
        return defaultInstance;
    }

    /**
     * 解压zip压缩文件
     * @param zipFile
     * @return
     */
    public static boolean unzip(File zipFile){
        return unzip(zipFile, true);
    }

    /**
     * 解压zip压缩文件
     * @param zipFile
     * @param delete 是否删除压缩文件
     * @return
     */
    public static boolean unzip(File zipFile, Boolean delete){
        if(zipFile != null && zipFile.exists()){
            ZipInputStream inZip = null;
            try {
                inZip = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry zipEntry;
                String entryName;
                File dir = zipFile.getParentFile();
                while ((zipEntry = inZip.getNextEntry()) != null) {
                    entryName = zipEntry.getName();
                    if (zipEntry.isDirectory()) {
                        File folder = new File(dir,entryName);
                        folder.mkdirs();
                    } else {
                        File file = new File(dir,entryName);
                        file.createNewFile();

                        FileOutputStream fos = new FileOutputStream(file);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = inZip.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            fos.flush();
                        }
                        fos.close();
                    }
                }
                if (delete){
                    deleteDir(zipFile);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }finally {
                try {
                    if(inZip != null){
                        inZip.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            return false;
        }
    }

    /**
     * 删除指定文件或者文件夹
     * @param dir
     */
    public static void deleteDir(File dir){
        if (dir==null||!dir.exists()) {
            return;
        } else {
            if (dir.isFile()) {
                dir.delete();
                return;
            }
        }
        if (dir.isDirectory()) {
            File[] childFile = dir.listFiles();
            if (childFile == null || childFile.length == 0) {
                dir.delete();
                return;
            }
            for (File f : childFile) {
                deleteDir(f);
            }
            dir.delete();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(AppCompatActivity activity, @NonNull String[] permissions, int requestCode) {
        return checkPermission(activity, permissions, requestCode, true);
    }

    /**
     * 权限检查
     * @param activity
     * @param permissions
     * @param requestCode
     * @param auth 是否弹出权限弹框
     * @return
     */
    public static boolean checkPermission(AppCompatActivity activity, @NonNull String[] permissions, int requestCode, boolean auth) {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission: permissions){
                if (activity.checkSelfPermission(permission) != PERMISSION_GRANTED){
                    isGranted = false;
                }
            }
            if (!isGranted && auth) {
                activity.requestPermissions(permissions, requestCode);
            }
        }
        return isGranted;
    }

    /*以下简陋版下载解压流程，尽量前端各自实现*/
    // TODO: 2019/7/25 以下简陋版下载解压流程，尽量前端各自实现
    public void toDownLoadBundle(String bundleUrl, FileDownloadManagerListener downloadManagerListener) {
        toDownLoadBundle(bundleUrl, new OkHttpDownload(), downloadManagerListener);
    }

    public void toDownLoadBundle(String bundleUrl, DownloadRequest downloadRequest, FileDownloadManagerListener downloadManagerListener) {
        new FileDownloadManager(getApplication(), bundleUrl)
                .setListener(downloadManagerListener)
                .setDownloadRequest(downloadRequest)
                .download();
    }

    private Application getApplication() {
        return RNBridge.getInstance().getApplication();
    }

}
