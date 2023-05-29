package com.example.mobilehelper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobilehelper.R;
import com.example.mobilehelper.util.MobileDataUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MobileFragment extends BaseFragment {

    private static final String TAG = "MobileFragment";
    private Context mContext;
    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.tv_rssi)
    TextView tv_rssi;

    @BindView(R.id.tv_slotCount)
    TextView tv_slotCount;

    @BindView(R.id.tv_simCount)
    TextView tv_simCount;

    @BindView(R.id.rl_itemStatus)
    RelativeLayout rl_itemStatus;
    @BindView(R.id.tv_sim_info)
    TextView tv_sim_info;
    private MobileDataUtil mMobileDataUtil;
    private TelephonyManager mTelephoMgr;
    private int mSimCount;
    private int slotCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_mobile, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initData() {
        mMobileDataUtil = new MobileDataUtil(mContext);
        mTelephoMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    protected void initView() {
        String status = mMobileDataUtil.getMobileDataStatus()?"开":"关";
        tv_status.setText(status);
        mSimCount = mMobileDataUtil.getSimCount();
        slotCount = mMobileDataUtil.getPhoneSlotCount();
        tv_slotCount.setText(mMobileDataUtil.getPhoneSlotCount()+"");
        tv_simCount.setText(mSimCount+"");
        tv_rssi.setText(mMobileDataUtil.getSignStrength()+"");
        String desc = "";
        for (int i = 0; i < slotCount; i++) {
            desc += mMobileDataUtil.getSimCardInfo(i);
        }
        tv_sim_info.setText(desc);
    }

    @OnClick(R.id.rl_itemStatus)
    public void setMobileStatus(){
        boolean isOpen = mMobileDataUtil.getMobileDataStatus();
        if (isOpen){
            mMobileDataUtil.setMobileDataEnabled(false);
        }else{
            mMobileDataUtil.setMobileDataEnabled(true);
        }

    }

    //监听
    private PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onDataConnectionStateChanged(int state) {
            super.onDataConnectionStateChanged(state);
            Log.d(TAG, "===========onDataConnectionStateChanged====state==========" + state);
            initView();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        /*String status = mMobileDataUtil.getMobileDataStatus()?"开":"关";
        tv_status.setText(status);*/
        mTelephoMgr.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTelephoMgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
