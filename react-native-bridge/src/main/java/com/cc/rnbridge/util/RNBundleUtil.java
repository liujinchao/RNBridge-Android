package com.cc.rnbridge.util;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.cc.rnbridge.RNBridge;

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
            synchronized (RNBridge.class) {
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
    private DownloadManager mDownLoadManager;
    private long mDownloadId;
    private UpdateBundleCallBack mUpdateBundleCb;
    private File mBundleZipFile;
    private final String KEY_BUNDLE_DEFAULT_PATH_FILE = "finalbundle"; //bundle.zip文件保存的默认文件夹名称
    private String mBundleFileName;
    /**
     * 下载对应的bundle.zip文件
     * @param bundleUrl 下载链接
     * @param updateBundleCallBack
     * 下载流程或权限检查尽量前端自己实现，此处仅提供的简陋版下载
     */
    @Deprecated
    public void downLoadBundle(String bundleUrl, UpdateBundleCallBack updateBundleCallBack){
        if (TextUtils.isEmpty(bundleUrl)){
            return;
        }
        mUpdateBundleCb = updateBundleCallBack;
        mBundleFileName = bundleUrl.substring(bundleUrl.lastIndexOf("/")+1);
        String defaultSavePath = getApplication().getExternalCacheDir()+"/" + KEY_BUNDLE_DEFAULT_PATH_FILE;
        mBundleZipFile = new File(defaultSavePath, mBundleFileName);
        //删除已下载的zip历史文件
        if (mBundleZipFile.exists()){
            RNBundleUtil.deleteDir(mBundleZipFile);
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(bundleUrl));
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.parse("file://"+mBundleZipFile.getAbsolutePath()));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        mDownLoadManager = (DownloadManager) getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadId = mDownLoadManager.enqueue(request);
        getApplication().registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private Application getApplication() {
        return RNBridge.getInstance().getApplication();
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();
        }
    };
    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mDownloadId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = mDownLoadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    System.out.println("下载暂停");
                case DownloadManager.STATUS_PENDING:
                    System.out.println("下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    System.out.println("正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    System.out.println("下载成功");
                    replaceBundle();
                    break;
                case DownloadManager.STATUS_FAILED:
                    System.out.println("下载失败");
                    break;
            }
        }
    }
    protected void replaceBundle() {
        String defaultSavePath = getApplication().getExternalCacheDir()+"/"+KEY_BUNDLE_DEFAULT_PATH_FILE;
        File reactDir = new File(defaultSavePath);
        if(!reactDir.exists()){
            reactDir.mkdirs();
        }
        if (mBundleZipFile == null || !mBundleZipFile.exists()){
            sendMsgToCb(null);
            return;
        }
        boolean result = RNBundleUtil.unzip(mBundleZipFile);
        if(result){//解压成功后保存当前最新bundle的版本
            if (!TextUtils.isEmpty(mBundleFileName) && mBundleFileName.contains(".")) {
                sendMsgToCb(defaultSavePath + "/"+mBundleFileName.substring(0, mBundleFileName.lastIndexOf(".")));
            }else {
                sendMsgToCb(defaultSavePath + "/"+mBundleFileName);
            }
        }else{//解压失败应该删除掉有问题的文件，防止RN加载错误的bundle文件
            sendMsgToCb(null);
            System.out.println("解压失败");
            File reactbundleDir = new File(defaultSavePath);
            RNBundleUtil.deleteDir(reactbundleDir);
        }
    }

    private void sendMsgToCb(String path){
        if (mUpdateBundleCb != null){
            mUpdateBundleCb.getUnZipBundlePath(path);
        }
    }

    public interface UpdateBundleCallBack {
        /**
         * 解压文件夹路径
         * @param path
         */
        void getUnZipBundlePath(String path);
    }

}
