package com.example.mobilehelper;


import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentContainerView;

import com.example.mobilehelper.constant.Constants;
import com.example.mobilehelper.fragment.BlueFragment;
import com.example.mobilehelper.fragment.InfoFragment;
import com.example.mobilehelper.fragment.MainFragment;
import com.example.mobilehelper.fragment.MobileFragment;
import com.example.mobilehelper.fragment.SpeedFragment;
import com.example.mobilehelper.fragment.WifiFragment;
import com.example.mobilehelper.util.PermissionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

   @BindView(R.id.iv_back)
    ImageView iv_back;

   @BindView(R.id.tv_title)
    TextView tv_title;

   @BindView(R.id.fcv_maincontain)
    FragmentContainerView fcv_maincontain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void initData() {
    }

    @OnClick(R.id.iv_back)
    public void switchMain(){
        switchPage(Constants.FUN_DEFAULT);
    }

    @Override
    protected void initView() {
        switchPage(Constants.FUN_DEFAULT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检测所有权限
        checkAllPermission();
    }

    private void checkAllPermission() {
        boolean isPermission = PermissionUtil.checkMultiPermission(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                }, R.id.fcv_maincontain % 4096);
        if (!isPermission){
            Toast.makeText(this, "需要允许权限才能正常使用哦", Toast.LENGTH_SHORT).show();
        }
    }




    public void switchPage(int pageType){
        iv_back.setVisibility(View.VISIBLE);
        switch (pageType) {
            case Constants.FUN_WIFI:
                tv_title.setText("WIFI小助理");
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_maincontain,
                        new WifiFragment(),WifiFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
            case Constants.FUN_MOBILE:
                tv_title.setText("移动数据");
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_maincontain,
                        new MobileFragment(),MobileFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
            case Constants.FUN_BLUE:
                tv_title.setText("蓝牙");
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_maincontain,
                        new BlueFragment(),BlueFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
            case Constants.FUN_SPEED:
                tv_title.setText("网络测速");
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_maincontain,
                        new SpeedFragment(),SpeedFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
            case Constants.FUN_INFO:
                tv_title.setText("基础信息");
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_maincontain,
                        new InfoFragment(),InfoFragment.class.getSimpleName()).commitAllowingStateLoss();
                break;
            default:
                iv_back.setVisibility(View.GONE);
                tv_title.setText("手机小管家");
                getSupportFragmentManager().beginTransaction().replace(R.id.fcv_maincontain,
                        new MainFragment(),MainFragment.class.getSimpleName()).commitAllowingStateLoss();
        }
    }


}