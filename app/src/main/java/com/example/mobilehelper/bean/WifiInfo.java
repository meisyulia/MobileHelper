package com.example.mobilehelper.bean;

public class WifiInfo {
    private String bssid;
    private String ssid;
    private int strength;//(0-4)
    private String capabilities;
    private String freRange;
    private int wifiStatus; //0:未连接  1：已保存  2：已连接
    private int linkSpeed;

    public WifiInfo(){

    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getFreRange() {
        return freRange;
    }

    public void setFreRange(String freRange) {
        this.freRange = freRange;
    }

    public int getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(int linkStatus) {
        this.wifiStatus = linkStatus;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }
}
