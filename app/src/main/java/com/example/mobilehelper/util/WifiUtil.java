package com.example.mobilehelper.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class WifiUtil {

    private static final String TAG = "WifiUtil";
    private final WifiManager wm;
    private final Context ctx;

    public WifiUtil(Context ctx){
        // 从系统服务中获取无线网络管理器
        wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        this.ctx = ctx;
    }

    // 获取无线网络的开关状态
    public boolean getWifiStatus() {
        return wm.isWifiEnabled();
    }

    // 打开或关闭无线网络
    public void setWifiStatus( boolean enabled) {
        wm.setWifiEnabled(enabled);  //Android10之后，普通应用不能开关WiFi
        //以 Android 10 或更高版本为目标平台的应用无法启用或停用 WLAN。WifiManager.setWifiEnabled() 方法始终返回 false
    }

    //获取WiFi信号强度
    public int getWifiStrength(int level) {
        int strength = WifiManager.calculateSignalLevel(level, 5);
        return strength;
    }

    //获取WiFi列表
    public List<ScanResult> getWifiScanResult() {
        List<ScanResult> newScanResults = new ArrayList<>();
        // 扫描WiFi列表信息
        wm.startScan();
        List<ScanResult> scanResults = wm.getScanResults();//搜索到的设备列表
        for (ScanResult scanResult : scanResults) {
            //将获取的信息按信号从大到小排列
            int position = getItemPosition(newScanResults, scanResult);
            if (position != -1) {
                if (newScanResults.get(position).level < scanResult.level) {
                    newScanResults.remove(position);
                    newScanResults.add(position, scanResult);
                }
            } else {
                newScanResults.add(scanResult);
            }
        }
        //同时如果有连接的设备放在列表的第一位
        String bssid = getConnectWifiBSSID();
        for (int i = 0; i < newScanResults.size(); i++) {
            ScanResult scanResult = newScanResults.get(i);
            if (TextUtils.equals(bssid, scanResult.BSSID)) {
                newScanResults.remove(i);
                newScanResults.add(0, scanResult);
            }
        }
        return newScanResults;
    }


    /**
     * 返回item在list中的坐标
     */
    private int getItemPosition(List<ScanResult> list, ScanResult item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.SSID.equals(list.get(i).SSID)) {
                return i;
            }
        }
        return -1;
    }

    //获取ssid相同的在list中的坐标
    public int getItemPosition(List<ScanResult> list, String ssid) {
        for (int i = 0; i < list.size(); i++) {
            if (ssid.equals(list.get(i).SSID)) {
                return i;
            }
        }
        return -1;
    }

    //获取已连接的WiFi的名称
    public String getConnectWifiSSID() {
        String ssid = "";
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID().replace("\"", ""); // 获取连接的WiFi名称
        }
        return ssid;
    }

    //获已连接的WiFi的BSSID
    public String getConnectWifiBSSID() {
        String bssid = "";
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if (wifiInfo != null) {
            bssid = wifiInfo.getBSSID(); // 获取连接的MAC
        }
        return bssid;
    }

    // 获取当前连接wifi的速度
    public int getConnWifiSpeed() {
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getLinkSpeed();
        } else {
            return 0;
        }

    }

    // 获取当前连接wifi的速度单位
    public String getConnWifiSpeedUnit() {
        return WifiInfo.LINK_SPEED_UNITS;
    }

    //判断是否需要密码
    public boolean isHasPwd(String capabilities){
        if (!TextUtils.isEmpty(capabilities)){
            if (capabilities.contains("WPA")) {
                return true;
            } else if (capabilities.contains("WEP")) {
                return true;
            } else if (capabilities.contains("EAP")){
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * 有密码连接
     * @param ssid
     * @param pws
     */
    public boolean connectWifiPws(String ssid, String pws){
        wm.disableNetwork(wm.getConnectionInfo().getNetworkId());
        int netId = wm.addNetwork(getWifiConfig(ssid, pws, true));
        boolean enableNetwork = wm.enableNetwork(netId, true);
        return enableNetwork;
    }

    /**
     * 无密码连接
     * @param ssid
     */
    public boolean connectWifiNoPws(String ssid) {
        wm.disableNetwork(wm.getConnectionInfo().getNetworkId());
        int netId = wm.addNetwork(getWifiConfig(ssid, "", false));
        boolean enableNetwork = wm.enableNetwork(netId, true);
        return enableNetwork;
    }

        //处理WiFi的加密类型
    public String getSecurityType(String capabilities) {
        String result = "";
        if (capabilities.contains("WPA")) {
            result = "通过WPA/WPA2进行保护";
        }else if (capabilities.contains("WEP")){
            result = "通过WEP进行保护";
        }else if (capabilities.contains("EAP")) {
            result = "通过EPA进行保护";
        } else if (capabilities.contains("ESS")) {
            result = "无";
        }
        if (capabilities.contains("WPS")) {
            result += "(可使用WPS)";
        }
        return result;
    }

    //删除WiFi配置信息

    /**
     * 删除连过WiFi的配置信息
     * @param wifiConfiguration
     * @return isRemoved:true 删除成功 false 删除失败
     */
    public boolean removeConfig(WifiConfiguration wifiConfiguration ){
        boolean isRemoved = wm.removeNetwork(wifiConfiguration.networkId);
        wm.saveConfiguration();
        return isRemoved;
    }

    //断开连接
    public void disconnectWifi(){
        wm.disableNetwork(wm.getConnectionInfo().getNetworkId());
        wm.disconnect();
    }

    //处理WiFi连接过程中返回的状态
    public String getConnectState(NetworkInfo info) {
        String result = "";
        if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
            result = "连接已断开";
        } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
            result = "已连接";
        } else {
            NetworkInfo.DetailedState state = info.getDetailedState();
            if (state == state.CONNECTING) {
                result = "连接中...";
            } else if (state == state.AUTHENTICATING) {
                result = "正在验证身份信息...";
            } else if (state == state.OBTAINING_IPADDR) {
                result = "正在获取IP地址...";
            } else if (state == state.FAILED) {
                result = "连接失败";
            }
        }
        return result;
    }

    //通过isExits（String SSID）方法判断系统是否保存着当前WiFi的信息。
    //获取到的WiFiConfiguration对象中，只有ssid和networkId是一定有的，可以用于直接连接该热点，其他信息如bssid，密钥等信息基本都是空的。
    public WifiConfiguration isExits(String SSID) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(ctx, "没有允许权限无法获取WiFi信息哦", Toast.LENGTH_SHORT).show();
            return null;
        }
        List<WifiConfiguration> existingConfigs = wm.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            //Log.i(TAG, "isExits: existingConfig.ssid="+existingConfig.SSID);
            String configSSID = existingConfig.SSID.replace("\"", "");
            if (TextUtils.equals(configSSID,SSID) ){
                return existingConfig;
            }
        }
        return null;
    }


    /**
     * 通过配置信息的networkId进行连接
     * @param config
     * @return true:连接成功 false:连接失败
     */
    public boolean connectByNetId(WifiConfiguration config){
        int networkId = config.networkId;
        boolean enableNetwork = wm.enableNetwork(networkId, true);
        return enableNetwork;
    }

    /**
     * wifi设置
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private WifiConfiguration getWifiConfig(String ssid, String pws, boolean isHasPws){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExits(ssid);
        if(tempConfig != null) {
            wm.removeNetwork(tempConfig.networkId);
        }
        if (isHasPws){
            config.preSharedKey = "\""+pws+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }


    /**
     * 判断wifi是否为2.4G
     * @param freq
     * @return
     */
    public static boolean is24GHz(int freq) {
        return freq > 2400 && freq < 2500;
    }

    /**
     * 判断wifi是否为5G
     * @param freq
     * @return
     */
    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }


}




