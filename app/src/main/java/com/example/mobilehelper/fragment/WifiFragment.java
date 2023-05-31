package com.example.mobilehelper.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.R;
import com.example.mobilehelper.adapter.WifiAllAdapter;
import com.example.mobilehelper.bean.WifiInfo;
import com.example.mobilehelper.util.ClickUtil;
import com.example.mobilehelper.util.GsonUtil;
import com.example.mobilehelper.util.WifiUtil;
import com.example.mobilehelper.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WifiFragment extends BaseFragment implements WifiAllAdapter.OnItemClickListener {

    private static final String TAG = "WifiFragment";
    private View mView;
    private Context mContext;

    @BindView(R.id.ck_status)
    CheckBox ck_status;

    @BindView(R.id.tv_connectWifi)
    TextView tv_connectWifi;

    @BindView(R.id.rv_allWifi)
    RecyclerView tv_allWifi;
    private Unbinder unbinder;
    private WifiManager mWifiMgr;
    public static int REQUEST_CODE_WIFI_PANEL = 4096;
    private WifiReceiver mWifiReceiver;
    private List<ScanResult> mScanResults = new ArrayList<>();
    private WifiAllAdapter mWifiAllAdapter;
    private WifiUtil mWifiUtil;
    private WifiLinkDlgFragment linkDlgFragment;
    private WifiInfoDlgFragment infoDlgFragment;
    private String mWifiState = "";
    private String mWifiSSID = "";
    private String mWifiBSSID = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_wifi, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mWifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiUtil = new WifiUtil(mContext);
        return mView;
    }


    @Override
    protected void initView() {
        ck_status.setChecked(mWifiUtil.getWifiStatus());
        ck_status.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            /*if (Build.VERSION.SDK_INT < 29){
                WifiUtil.setWifiStatus(mContext,isChecked); //在Android10以上已不起效
            }else {
                //Android10以上如何处理？
                // 打开系统的快速设置面板 方法一：
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                   startActivity(panelIntent);
            }*/
            if (ClickUtil.isFastClick()){
                return;
            }
            mWifiUtil.setWifiStatus(isChecked); //在Android10以上已不起效 ，将targetSdk版本设置到28，可以触发权限判断（卡了我好久！！！！！）

        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        tv_allWifi.setLayoutManager(linearLayoutManager);
        mWifiAllAdapter.setOnItemClickListener(this);
        tv_allWifi.setAdapter(mWifiAllAdapter);
        tv_allWifi.setItemAnimator(new DefaultItemAnimator());
        tv_allWifi.addItemDecoration(new SpacesItemDecoration(2,mContext.getResources().getColor(R.color.black)));

        //注册WiFi扫描结果、WiFi状态变化的广播接收
        mWifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); //监听wifi扫描结果
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //监听wifi是开关变化的状态
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); // 监听网络状态变化的广播
        //intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION); //监听连接结果
        //intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION); //WiFi网络ID列表发生变化时,没起效
        mContext.registerReceiver(mWifiReceiver, intentFilter);
    }

    @Override
    protected void initData() {
        // 扫描WiFi列表信息
        /*mWifiMgr.startScan();
        mScanResults = mWifiMgr.getScanResults();*/
        mScanResults = mWifiUtil.getWifiScanResult();
        mWifiAllAdapter = new WifiAllAdapter(mContext,mScanResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        //长时间不进去，进行是当前页面时再获取一次
        refreshScanList();
    }

    @Override
    public void onItemClick(View v, WifiInfo info) {
        int wifiStatus = info.getWifiStatus();
        String wifiInfo = GsonUtil.toJsonString(info);
        if (wifiStatus==0){
            if ((linkDlgFragment!= null &&linkDlgFragment.isResumed() )|| ClickUtil.isFastClick(800)){
                return;
            }
            linkDlgFragment = WifiLinkDlgFragment.newInstance(wifiInfo);
            linkDlgFragment.showNow(getActivity().getSupportFragmentManager(), WifiLinkDlgFragment.class.getSimpleName());
            linkDlgFragment.setCancelable(false);
        }else{
            if ((infoDlgFragment!=null && infoDlgFragment.isResumed()) || ClickUtil.isFastClick(800)){
                return;
            }
            infoDlgFragment = WifiInfoDlgFragment.newInstance(wifiInfo);
            infoDlgFragment.showNow(getActivity().getSupportFragmentManager(), WifiInfoDlgFragment.class.getSimpleName());
            infoDlgFragment.setCancelable(false);
        }
    }



    //WiFi广播接收器
    private class WifiReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                // 获取WiFi列表信息
                refreshScanList();
            }else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                // 获取WiFi列表信息
                /*mScanResults = mWifiUtil.getWifiScanResult();
                mWifiAllAdapter.setScanResults(mScanResults);*/
                showWifiState(wifiState);
            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                    ) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                String state = "";
                String ssid = "";
                String bssid = "";
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) { //在连接中或已连接才有ssid,否者ssid显示为：<unknown ssid>
                    /*WifiInfo wifiInfo = mWifiMgr.getConnectionInfo();
                    String ssid = wifiInfo.getSSID().replace("\"", ""); // 获取已连接WiFi名称*/
                    refreshScanList();
                    state = mWifiUtil.getConnectState(networkInfo);
                    ssid = mWifiUtil.getConnectWifiSSID();
                    bssid = mWifiUtil.getConnectWifiBSSID();
                    mWifiAllAdapter.setOtherInfo(state,ssid);

                }
                if (networkInfo !=null && networkInfo.isConnected()){
                    tv_connectWifi.setText(ssid);
                } else {
                    tv_connectWifi.setText("未连接WIFI");
                }

                /*mScanResults = mWifiUtil.getWifiScanResult();
                mWifiAllAdapter.setScanResults(mScanResults);*/
            }
        }
    }

    private void refreshScanList() {
        mScanResults = mWifiUtil.getWifiScanResult();
        mWifiAllAdapter.setScanResults(mScanResults);
        // 重新扫描WiFi列表信息
        mWifiMgr.startScan();
    }


    private void showWifiState(int  wifiState) {
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLING:
                showTips("WIFI正在关闭");
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                ck_status.setChecked(false);
                showTips("WIFI已关闭");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                showTips("WIFI正在打开");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                ck_status.setChecked(true);
                showTips("WIFI已打开");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mContext.unregisterReceiver(mWifiReceiver);
    }


}
