package com.example.mobilehelper.util;

public class ClickUtil {

    private static long lastClickTime = 0;
    private static final int INTERVAL_TIME = 1500; // 点击间隔时间，单位毫秒

    /**
     * 判断是否为快速点击
     * @param interval：连续点击的间隔时间
     * @return true: 是快速点击，false: 不是快速点击
     */
    public static boolean isFastClick(int interval) {
        int interval_time = interval;
        if (interval==0){
            interval_time = INTERVAL_TIME;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < interval_time) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }

    /**
     * 判断是否为快速点击
     *
     * @return true: 是快速点击，false: 不是快速点击
     */
    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < INTERVAL_TIME) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
