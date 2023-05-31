package com.example.mobilehelper.bean;

import android.net.wifi.ScanResult;

import java.util.List;

public class NewWifiInfo {
    private List<ScanResult> scanResult;
    private String tagDescribe;

    public NewWifiInfo(){

    }

    public List<ScanResult> getScanResult() {
        return scanResult;
    }

    public void setScanResult(List<ScanResult> scanResult) {
        this.scanResult = scanResult;
    }

    public String getTagDescribe() {
        return tagDescribe;
    }

    public void setTagDescribe(String tagDescribe) {
        this.tagDescribe = tagDescribe;
    }
}
