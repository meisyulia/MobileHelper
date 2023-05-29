package com.example.mobilehelper.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobilehelper.R;
import com.example.mobilehelper.bean.WifiInfo;
import com.example.mobilehelper.util.GsonUtil;
import com.example.mobilehelper.util.Utils;
import com.example.mobilehelper.util.WifiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WifiLinkDlgFragment extends DialogFragment {

    @BindView(R.id.tv_ssid)
    TextView tv_ssid;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.btn_linkWifi)
    Button btn_linkWifi;
    private Context mContext;
    private View mView;
    private Unbinder unbinder;
    private String mWifiInfoStr;
    private WifiInfo mWifiInfo;
    private WifiUtil mWifiUtil;
    private boolean mIsHasPwd;
    private Window mWindow;
    private String mSSID;

    public static WifiLinkDlgFragment newInstance(String wifiInfo){
        WifiLinkDlgFragment fragment = new WifiLinkDlgFragment();
        Bundle bundle = new Bundle();
        bundle.putString("wifi_info",wifiInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        if (getArguments()!= null){
            mWifiInfoStr = getArguments().getString("wifi_info");
        }
        mView = inflater.inflate(R.layout.dialog_input_pass, container, false);
        unbinder = ButterKnife.bind(this, mView);
        initData();
        initView();
        return mView;
    }

    private void initView() {
        tv_ssid.setText(mSSID);
    }

    private void initData() {
        mWifiUtil = new WifiUtil(mContext);
        mWifiInfo = GsonUtil.parserJsonToArrayBean(mWifiInfoStr, WifiInfo.class);
        mIsHasPwd = mWifiUtil.isHasPwd(mWifiInfo.getCapabilities());
        mSSID= mWifiInfo.getSsid();
    }

    @Override
    public void onResume() {
        super.onResume();
        initWindow();
    }

    private void initWindow() {
        mWindow = getDialog().getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setLayout((int) (Utils.getScreenWidth(mContext)*0.9),ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_linkWifi})
    public void onBindClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_linkWifi:
                linkWifi();
                dismiss();
                break;
        }
    }

    private void linkWifi() {
        String password = et_password.getText().toString();
        //进行连接
        if (mIsHasPwd){
            //判断密码是否为空
            if (TextUtils.isEmpty(password)){
                Toast.makeText(mContext, "密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isSuccess = mWifiUtil.connectWifiPws(mSSID, password);
            showLinkResult(isSuccess);
        }else{
            boolean isSuccess = mWifiUtil.connectWifiNoPws(mSSID);
            showLinkResult(isSuccess);
        }
    }

    private void showLinkResult(boolean isSuccess) {
        String desc = isSuccess?"连接成功":"连接失败";
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }
}
