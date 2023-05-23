package com.example.mobilehelper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobilehelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InfoFragment extends BaseFragment {

    private Context mContext;
    private View mView;
    private Unbinder unbinder;

    @BindView(R.id.tv_battery)
    TextView tv_battery;

    @BindView(R.id.tv_romSize)
    TextView tv_romSize;

    @BindView(R.id.tv_sdSize)
    TextView tv_sdSize;

    @BindView(R.id.tv_volume)
    TextView tv_volume;

    @BindView(R.id.tv_brightness)
    TextView tv_brightness;

    @BindView(R.id.sb_volume)
    SeekBar sb_volume;

    @BindView(R.id.sb_brightness)
    SeekBar sb_brightness;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_info, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
