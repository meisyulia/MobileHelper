package com.example.mobilehelper.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

//原文链接：https://blog.csdn.net/WX_LYB/article/details/52614180
public class StorageUtil {
    private static Timer timer;
    private static long INTERVAL_TIME = 5000;
    private static String TAG = "StorageUtil";

    public static long GB = 1024 * 1024 * 1024;
    final static long[] deviceRomMemoryMap = {2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
    static String[] displayRomSize = {"2 GB","4 GB","8 GB","16 GB","32 GB","64 GB","128 GB","256 GB","512 GB","1024 GB","2048 GB"};




    // 获取总存储大小
    public static String getExTotalSize(Context context) {
        // 获取根目录
        File path = Environment.getExternalStorageDirectory();
        Log.i(TAG, "getExTotalSize: path.getPath()="+path.getPath());
        Log.i(TAG, "getExTotalSize: path.getAbsolutePath()="+path.getAbsolutePath());
        Log.i(TAG, "getExTotalSize: Environment.getDataDirectory().getPath()="+Environment.getDataDirectory().getPath());
        // 获取指定目录下的内存存储状态
        StatFs stat = new StatFs(path.getPath());
        long totalSize = stat.getTotalBytes(); //没有包括系统占用的空间,比手机显示的总内存小
        Log.i(TAG, "getSDTotalSize: totalSize="+totalSize);
        int i;
        for (i = 0; i < deviceRomMemoryMap.length; i++) {
            if (totalSize<=deviceRomMemoryMap[i]){
                break;
            }
            if(i == deviceRomMemoryMap.length) {
                i--;
            }
        }
        return displayRomSize[i]; //有些手机可能是除1000或1024看手机
    }
    // 获取存储的可用大小
    public static String getExAvailableSize(Context context) {
        // 获取根目录
        File path = Environment.getExternalStorageDirectory();
        // 获取指定目录下的内存存储状态
        StatFs stat = new StatFs(path.getPath());
        // 可用空间 = 扇区的大小 + 可用的扇区
        long availableSize = stat.getAvailableBytes();
        // 格式化文件大小的格式
        return Formatter.formatFileSize(context,availableSize);
    }
    // 判断sd卡是否可用
    public static boolean isSdSafe(Context context) {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeState = StorageManager.class.getMethod("getVolumeState", new Class[]{String.class});
            String state = (String) getVolumeState.invoke(sm, getSDPath(context));
            if (state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
    //获取外置sd卡路径
    public static String getSDPath(Context context){
        /*File[] fileList = context.getExternalFilesDirs(null);
        for (int i = 0; i < fileList.length; i++) {
            Log.i(TAG, "getSDPath: path="+fileList[i].getPath()); //可以获取全部的外部存储路径
        }*/
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePaths = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            Object[] params = {};
            String[] paths = (String[]) getVolumePaths.invoke(sm, params);
            if (paths.length==1){
                return null;
            }else{
                Log.i(TAG, "getSDPath: paths="+paths[1]);
                return paths[1];
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    //外置sd卡的总大小
    public static String getSDTotalSize(Context context){
        if (isSdSafe(context)){
            // 获取指定目录下的内存存储状态
            StatFs stat = new StatFs(getSDPath(context));
            long totalSize = stat.getTotalBytes();
            return Formatter.formatFileSize(context,totalSize);
        }
        return "";
    }
    //外置sd卡可用大小
    public static String getSDAvailableSize(Context context){
        if (isSdSafe(context)){
            // 获取指定目录下的内存存储状态
            StatFs stat = new StatFs(getSDPath(context));
            // 可用空间 = 扇区的大小 + 可用的扇区
            long availableSize = stat.getAvailableBytes();
            // 格式化文件大小的格式
            return Formatter.formatFileSize(context,availableSize);
        }
        return "";
    }
    // 获取存储根目录字符串的路径
    public static String getSdPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static File getSdFile() {
        return Environment.getExternalStorageDirectory();
    }

    //开始监测
    public static void startMonitoring(Context context,OnStorageChangeListener listener){
        if (timer == null){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (context != null){
                        String exAvailableSize = getExAvailableSize(context);
                        String sdAvailableSize = getSDAvailableSize(context);
                        if (listener!= null){
                            listener.onStorageChange(exAvailableSize,sdAvailableSize);
                        }
                    }

                }
            },0,INTERVAL_TIME);
        }
    }
    //停止监测
    public static void stopMonitoring(){
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }
    //监听器,监听可用空间的变化
    public interface OnStorageChangeListener{
        void onStorageChange(String exAvailableSize,String SDAvailableSize);
    }



}
