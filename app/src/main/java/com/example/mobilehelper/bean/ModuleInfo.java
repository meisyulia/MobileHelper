package com.example.mobilehelper.bean;

import com.example.mobilehelper.R;
import com.example.mobilehelper.constant.Constants;

import java.util.ArrayList;

public class ModuleInfo {
    private int ic_id;
    private String title;
    private int tag;

    public ModuleInfo(int ic_id, String title, int tag) {
        this.ic_id = ic_id;
        this.title = title;
        this.tag = tag;
    }


    public int getIc_id() {
        return ic_id;
    }

    public void setIc_id(int ic_id) {
        this.ic_id = ic_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    private static int[] iconImageArray = {R.drawable.wifi_48,R.drawable.mobile_48,R.drawable.blue_48,
    R.drawable.speed_48,R.drawable.store_48};

    private static String[] titleArray = {"WIFI小助理","移动数据","蓝牙","网络测速","基础信息"};

    private static int[] tagArray = {Constants.FUN_WIFI,Constants.FUN_MOBILE,Constants.FUN_BLUE,Constants.FUN_SPEED,
    Constants.FUN_INFO};

    public static ArrayList<ModuleInfo> getDefaultModList(){
        ArrayList<ModuleInfo> listArray = new ArrayList<>();
        for (int i = 0; i < titleArray.length; i++) {
            listArray.add(new ModuleInfo(iconImageArray[i],titleArray[i],tagArray[i]));
        }
        return listArray;
    }
}
