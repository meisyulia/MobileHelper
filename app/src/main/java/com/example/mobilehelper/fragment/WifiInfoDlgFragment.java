package com.example.mobilehelper.fragment;

import static com.example.mobilehelper.constant.WifiConstant.wifiStatus;
import static com.example.mobilehelper.constant.WifiConstant.wifiStrengthStr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobilehelper.R;
import com.example.mobilehelper.bean.WifiInfo;
import com.example.mobilehelper.constant.WifiConstant;
import com.example.mobilehelper.util.GsonUtil;
import com.example.mobilehelper.util.Utils;
import com.example.mobilehelper.util.WifiUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WifiInfoDlgFragment extends DialogFragment {

    @BindView(R.id.tv_ssid)
    TextView tv_ssid;
    @BindView(R.id.tv_linkStatus)
    TextView tv_linkStatus;
    @BindView(R.id.rl_linkStatus)
    RelativeLayout rl_linkStatus;
    @BindView(R.id.tv_sigStrength)
    TextView tv_sigStrength;
    @BindView(R.id.rl_sigStrength)
    RelativeLayout rl_sigStrength;
    @BindView(R.id.tv_linkSpeed)
    TextView tv_linkSpeed;
    @BindView(R.id.rl_linkSpeed)
    RelativeLayout rl_linkSpeed;
    @BindView(R.id.tv_freqRange)
    TextView tv_freqRange;
    @BindView(R.id.rl_freqRange)
    RelativeLayout rl_freqRange;
    @BindView(R.id.tv_encType)
    TextView tv_encType;
    @BindView(R.id.rl_encType)
    RelativeLayout rl_encType;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.btn_delete)
    Button btn_delete;
    @BindView(R.id.btn_link)
    Button btn_link;
    private WifiInfo mInfoArray = null;
    private Context mContext;
    private View mView;
    private Unbinder unbinder;
    private String mWifiInfoStr;
    private Window mWindow;
    private int mWifiStatus;
    private WifiUtil mWifiUtil;
    private WifiConfiguration mWifiFig;


    public static WifiInfoDlgFragment newInstance(String wifiInfo){
        WifiInfoDlgFragment fragment = new WifiInfoDlgFragment();
        Bundle bundle = new Bundle();
        bundle.putString("wifi_info",wifiInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        if (getArguments()!=null){
            mWifiInfoStr = getArguments().getString("wifi_info");
        }
        mView = inflater.inflate(R.layout.dialog_wifi_info, container, false);
        unbinder = ButterKnife.bind(this, mView);
        initData();
        initView();
        return mView;
    }

    private void initData() {
        mInfoArray = GsonUtil.parserJsonToArrayBean(mWifiInfoStr,WifiInfo.class);
        mWifiStatus = mInfoArray.getWifiStatus();
        mWifiUtil = new WifiUtil(mContext);
        mWifiFig = mWifiUtil.isExits(mInfoArray.getSsid());
    }

    private void initView() {
        ctrlShow(mWifiStatus);
        tv_ssid.setText(mInfoArray.getSsid());
        tv_linkStatus.setText(wifiStatus[mInfoArray.getWifiStatus()]);
        tv_sigStrength.setText(wifiStrengthStr[mInfoArray.getStrength()]);
        tv_linkSpeed.setText(mInfoArray.getLinkSpeed()+"");
        tv_freqRange.setText(mInfoArray.getFreRange());
        tv_encType.setText(mInfoArray.getCapabilities());
    }

    private void ctrlShow(int wifiStatus) {
        if (wifiStatus==1){ //已保存
            btn_link.setVisibility(View.VISIBLE);
            rl_linkStatus.setVisibility(View.GONE);
            rl_linkSpeed.setVisibility(View.GONE);
            rl_freqRange.setVisibility(View.GONE);
        }else if (wifiStatus==2){ //已连接
            btn_link.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_cancel)
    public void cancel(){
        dismiss();
    }

    @OnClick(R.id.btn_delete)
    public void deleteWifi(){
        /*if (mWifiStatus==2){
            if (mWifiFig!= null){
                boolean isRemoved = mWifiUtil.removeConfig(mWifiFig);
                showRemoveResult(isRemoved);
            }
            mWifiUtil.disconnectWifi();

        }else if (mWifiStatus==1){
            if (mWifiFig!= null){
                boolean isRemoved = mWifiUtil.removeConfig(mWifiFig);
                showRemoveResult(isRemoved);
            }
        }*/
        if (mWifiFig!= null){
            boolean isRemoved = mWifiUtil.removeConfig(mWifiFig);
            showRemoveResult(isRemoved);
        }
        mWifiUtil.disconnectWifi();
        dismiss();
    }

    private void showRemoveResult(boolean isRemoved) {
        if (isRemoved){
            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_link)
    public void linkWifi(){
        //有保存信息的情况下连接WiFi
        if (mWifiFig!=null){
            boolean isSuccess = mWifiUtil.connectByNetId(mWifiFig);
            if(!isSuccess){
                String desc = "连接失败";
                Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
            }

        }
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        initWindow();
    }

    private void initWindow() {
        mWindow = getDialog().getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setLayout((int) (Utils.getScreenWidth(mContext)*0.95),ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
