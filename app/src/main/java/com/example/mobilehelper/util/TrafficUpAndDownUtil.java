package com.example.mobilehelper.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import java.math.BigDecimal;

public class TrafficUpAndDownUtil {

    private static long mLastTotalUp = 0l;
    private static long mLastTimeUp = 0l;
    private static long mCurrentTotalUp;
    private static BigDecimal mTotalUpSpeed;
    private static long mLastTotalDown = 0l;
    private static long mCurrentTotalDown;
    private static long mLastTimeDown = 0l;
    private static BigDecimal mTotalDownSpeed;

    //判断当前网络是否可用
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static BigDecimal getTotalUpSpeed(){
        long currentTotalTxBytes = TrafficStats.getTotalTxBytes();
        long currentTime = System.currentTimeMillis();
        mCurrentTotalUp = currentTotalTxBytes - mLastTotalUp;
        //计算
        mTotalUpSpeed = new BigDecimal((mCurrentTotalUp / 1024) * 1000 / (currentTime - mLastTimeUp) * 1.0);
        mLastTotalUp = currentTotalTxBytes;
        mLastTimeUp = currentTime;
        return mTotalUpSpeed;
    }

    public static BigDecimal getTotalDownSpeed(){
        long currentTotalRxBytes = TrafficStats.getTotalRxBytes();
        long currentTime = System.currentTimeMillis();
        mCurrentTotalDown = currentTotalRxBytes - mLastTotalDown;
        //计算
        mTotalDownSpeed = new BigDecimal((mCurrentTotalDown / 1024) * 1000 / (currentTime - mLastTimeDown) * 1.0);
        mLastTotalDown = currentTotalRxBytes;
        mLastTimeDown = currentTime;
        return mTotalDownSpeed;
    }
}
