package com.example.mobilehelper.adapter;




import static com.example.mobilehelper.util.WifiUtil.is24GHz;
import static com.example.mobilehelper.util.WifiUtil.is5GHz;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.R;
import com.example.mobilehelper.bean.WifiInfo;
import com.example.mobilehelper.constant.WifiConstant;
import com.example.mobilehelper.util.WifiUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiAllAdapter extends RecyclerView.Adapter {

    private static final String TAG = "WifiAllAdapter";
    private final Context mContext;
    private final WifiUtil mWifiUtil;
    private List<ScanResult> mScanResults;
    private String wifiState;
    private String connectedSSID;

    public WifiAllAdapter(Context context,List<ScanResult> ScanResults){
        mContext = context;
        mWifiUtil = new WifiUtil(mContext);
        mScanResults = ScanResults;
        wifiState = "";
        connectedSSID = "";
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_all_wifi, parent, false);
        return new ItemHolder(v);
    }

    public void setScanResults(List<ScanResult> scanResults) {
        mScanResults = scanResults;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScanResult scanResult = mScanResults.get(position);
        WifiInfo wifiInfo = getWifiInfo(scanResult);
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.tv_ssid.setText(wifiInfo.getSsid());
        //Log.i(TAG, "onBindViewHolder: wifiState="+wifiState+",connectedSSID="+connectedSSID);
        //Log.i(TAG, "onBindViewHolder: wifiInfo.getWifiStatus()="+wifiInfo.getWifiStatus());
        if (TextUtils.equals(wifiInfo.getSsid(),connectedSSID) && !TextUtils.isEmpty(wifiState)){
            itemHolder.tv_otherInfo.setText(wifiState);
        }else if (wifiInfo.getWifiStatus()==1){
            itemHolder.tv_otherInfo.setText("已保存,"+mWifiUtil.getSecurityType(wifiInfo.getCapabilities()));
        }else{
            itemHolder.tv_otherInfo.setText(mWifiUtil.getSecurityType(wifiInfo.getCapabilities()));
        }
        itemHolder.iv_strength.setImageResource(WifiConstant.wifiStrengthIcon[wifiInfo.getStrength()]);
        itemHolder.rv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(v,wifiInfo);
                }
            }
        });
    }

    public void setOtherInfo(String wifiState,String connectedSSID){
        Log.i(TAG, "setOtherInfo: wifiState="+wifiState+",connectedSSID="+connectedSSID);
        this.wifiState = wifiState;
        this.connectedSSID = connectedSSID;
        notifyDataSetChanged();
    }

    private WifiInfo getWifiInfo(ScanResult scanResult){
        WifiInfo wifiInfo = new WifiInfo();
        String bssid = scanResult.BSSID; //类似Mac地址
        String ssid = scanResult.SSID; // 获取WiFi名称
        String capabilities = scanResult.capabilities; // 获取WiFi安全性
        int level = scanResult.level; // 获取WiFi信号强度
        int strength = mWifiUtil.getWifiStrength(level);//获取强度等级（0-4）
        int frequency = scanResult.frequency; //获取频率
        String freRange = mWifiUtil.getFreRange(frequency);
        /*String info = "SSID: " + ssid + "\n" + "Capabilities: " + capabilities + "\n" + "Level: " + level
                +"\n" + "Strength:"+strength+"\n" + "Frequency:"+frequency;
        Log.i(TAG, "getWifiInfo: info="+info);*/
        WifiConfiguration wifiConfiguration = mWifiUtil.isExits(ssid);
        if (TextUtils.equals(ssid,connectedSSID) && TextUtils.equals("已连接",wifiState)){
            wifiInfo.setWifiStatus(2);
            wifiInfo.setLinkSpeed(mWifiUtil.getConnWifiSpeed());
        }else if (wifiConfiguration!=null){
            wifiInfo.setWifiStatus(1);
        }else{
            wifiInfo.setWifiStatus(0);
        }
        wifiInfo.setBssid(bssid);
        wifiInfo.setSsid(ssid);
        wifiInfo.setCapabilities(mWifiUtil.getSecurityType(capabilities));
        wifiInfo.setStrength(strength);
        wifiInfo.setFreRange(freRange);
        return wifiInfo;
    }

    /*private String getFreRange(int frequency) {
        if (is24GHz(frequency)){
            return "2.4GHz";
        }else if (is5GHz(frequency)){
            return "5GHz";
        }else{
            return "未知";
        }
    }*/

    @Override
    public int getItemCount() {
        return mScanResults.size();
    }



    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_item)
        RelativeLayout rv_item;

        @BindView(R.id.tv_ssid)
        TextView tv_ssid;

        @BindView(R.id.tv_otherInfo)
        TextView tv_otherInfo;

        @BindView(R.id.iv_strength)
        ImageView iv_strength;
        public ItemHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }
    public interface OnItemClickListener{
        void onItemClick(View v,WifiInfo info);
    }


}
