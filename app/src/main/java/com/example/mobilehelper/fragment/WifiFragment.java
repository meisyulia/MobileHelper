package com.example.mobilehelper.fragment;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.R;
import com.example.mobilehelper.util.SwitchUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WifiFragment extends BaseFragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_wifi, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initView() {
        ck_status.setChecked(SwitchUtil.getWlanStatus(mContext));
        ck_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SwitchUtil.setWlanStatus(mContext,isChecked);
            }
        });
    }

    @Override
    protected void initData() {
        mWifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
