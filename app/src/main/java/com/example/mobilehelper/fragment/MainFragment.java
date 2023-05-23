package com.example.mobilehelper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.R;
import com.example.mobilehelper.adapter.ModuleAdapter;
import com.example.mobilehelper.bean.ModuleInfo;
import com.example.mobilehelper.util.VersionUtil;
import com.example.mobilehelper.widget.SpacesItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends BaseFragment{

    private View mView;

    @BindView(R.id.rv_main)
    RecyclerView rv_main;

    @BindView(R.id.tv_version)
    TextView tv_version;
    private Unbinder unbinder;
    private Context mContext;
    private ModuleAdapter mModAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initView() {
        tv_version.setText("V-"+ VersionUtil.getVersionName(mContext));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rv_main.setLayoutManager(linearLayoutManager);
        rv_main.setAdapter(mModAdapter);
        rv_main.setItemAnimator(new DefaultItemAnimator());
        rv_main.addItemDecoration(new SpacesItemDecoration(1));

    }

    @Override
    protected void initData() {
        mModAdapter = new ModuleAdapter(mContext, ModuleInfo.getDefaultModList());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
