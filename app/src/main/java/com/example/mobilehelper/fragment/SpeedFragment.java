package com.example.mobilehelper.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mobilehelper.R;
import com.example.mobilehelper.util.DataUtil;
import com.example.mobilehelper.util.NetworkSpeedTestUtil;
import com.example.mobilehelper.util.TrafficUpAndDownUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SpeedFragment extends BaseFragment{

    private Context mContext;
    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.tv_upSpeed)
    TextView tv_upSpeed;

    @BindView(R.id.tv_downSpeed)
    TextView tv_downSpeed;
    private NetworkSpeedTestUtil mNetworkSpeedUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_speed, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
       mNetworkSpeedUtil = new NetworkSpeedTestUtil(mContext);
        mIsAvailable = mNetworkSpeedUtil.isNetworkAvailable();
        //mIsAvailable = TrafficUpAndDownUtil.isNetworkAvailable(mContext);
    }

    @Override
    protected void initView() {
        tv_status.setText(mIsAvailable?"可用":"不可用");
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mNetworkTest);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mNetworkTest);
    }

    private Handler mHandler = new Handler();
    private boolean mIsAvailable;
    private double mUploadSpeed;
    private double mDownloadSpeed;
    /*private float mUploadSpeed;
            private float mDownloadSpeed;*/
    private BigDecimal totalDownSpeed;
    private BigDecimal totalUpSpeed;
    private Runnable mNetworkTest = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            //updateNetworkSpeed();
            new Thread(mGetSpeed).start();
            //mHandler.postDelayed(this,1000);
        }
    };

    private Runnable mGetSpeed = new Runnable() {
        @Override
        public void run() {
           /* mIsAvailable = mNetworkSpeedUtil.isNetworkAvailable();
            mUploadSpeed = mNetworkSpeedUtil.getUploadSpeed();
            mDownloadSpeed = mNetworkSpeedUtil.getDownloadSpeed();*/
            mIsAvailable = TrafficUpAndDownUtil.isNetworkAvailable(mContext);
            totalUpSpeed = TrafficUpAndDownUtil.getTotalUpSpeed();
            totalDownSpeed = TrafficUpAndDownUtil.getTotalDownSpeed();
            mHandler.post(mShowText);
            mHandler.postDelayed(this,1000);
        }
    };

    private Runnable mShowText = new Runnable() {
        @Override
        public void run() {
            if (tv_status!=null && tv_downSpeed!=null && tv_upSpeed!=null){
                tv_status.setText(mIsAvailable?"可用":"不可用");
                if (mIsAvailable){
                    /*tv_downSpeed.setText(mDownloadSpeed+"k/s");
                    tv_upSpeed.setText(mUploadSpeed+"k/s");*/
                    /*tv_downSpeed.setText(String.format("%.2fk/s",mDownloadSpeed));
                    tv_upSpeed.setText(String.format("%.2fk/s",mUploadSpeed));*/
                    tv_downSpeed.setText(String.format("%sk/s", DataUtil.getTwoBigDecimal(totalDownSpeed).toString()));
                    tv_upSpeed.setText(String.format("%sk/s",DataUtil.getTwoBigDecimal(totalUpSpeed).toString()));
                    /*tv_downSpeed.setText(totalDownSpeed.toString()+"k/s");
                    tv_upSpeed.setText(totalUpSpeed.toString()+"k/s");*/
                }else{
                    tv_downSpeed.setText("0.0k/s");
                    tv_upSpeed.setText("0.0k/s");
                }
            }
        }
    };

    /*private void updateNetworkSpeed() {
        boolean isAvailable = mNetworkSpeedUtil.isNetworkAvailable();
        tv_status.setText(isAvailable?"可用":"不可用");
        if (isAvailable){
            float uploadSpeed = mNetworkSpeedUtil.getUploadSpeed();
            float downloadSpeed = mNetworkSpeedUtil.getDownloadSpeed();
            tv_downSpeed.setText(downloadSpeed+"k/s");
            tv_upSpeed.setText(uploadSpeed+"k/s");
        }else{
            tv_downSpeed.setText("0.0k/s");
            tv_upSpeed.setText("0.0k/s");
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }
}
