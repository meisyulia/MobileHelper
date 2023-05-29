package com.example.mobilehelper.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothDevice.BOND_BONDED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilehelper.R;
import com.example.mobilehelper.util.BluetoothUtil;
import com.example.mobilehelper.util.ClickUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BlueFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "BlueFragment";
    @BindView(R.id.ck_status)
    CheckBox ck_status;
    @BindView(R.id.tv_connectBluetooth)
    TextView tv_connectBluetooth;
    @BindView(R.id.rv_bondList)
    RecyclerView rv_bondList;
    @BindView(R.id.rv_searchList)
    RecyclerView rv_searchList;
    private Context mContext;
    private View mView;
    private Unbinder unbinder;
    private BluetoothUtil mBlueUtil;
    private int BLUETOOTH_RESPONSE = 1;
    private BluetoothReceiver mBlueReceiver;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mBoundedDeviceList = new ArrayList<>();
    private ArrayList<BluetoothDevice> mSearchDeviceList = new ArrayList<>();
    private BluetoothDevice mConnectedDevice;
    private com.example.mobilehelper.adapter.BluetoothAdapter mBondAdapter;
    private com.example.mobilehelper.adapter.BluetoothAdapter mSearchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_blue, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    protected void initData() {
        mBlueUtil = new BluetoothUtil(mContext);
        //初始化蓝牙适配器
        mBluetoothAdapter = mBlueUtil.initBluetooth();
        mBondAdapter = new com.example.mobilehelper.adapter.BluetoothAdapter(mContext, mBoundedDeviceList);
        mSearchAdapter = new com.example.mobilehelper.adapter.BluetoothAdapter(mContext, mSearchDeviceList);
    }

    @Override
    protected void initView() {
        ck_status.setChecked(mBlueUtil.getBlueToothStatus());
        ck_status.setOnCheckedChangeListener(this);
        LinearLayoutManager bondLLM = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rv_bondList.setLayoutManager(bondLLM);
        rv_bondList.setAdapter(mBondAdapter);
        rv_bondList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager searchLLM = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        rv_searchList.setLayoutManager(searchLLM);
        rv_searchList.setAdapter(mSearchAdapter);
        rv_searchList.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onResume() {
        super.onResume();
        initDiscovery();
        initReceiver();
    }

    private void initDiscovery() {
        if (mBlueUtil.getBlueToothStatus()){
            //正在开启
            mBlueUtil.beginDiscovery();
        }else{
            mBlueUtil.cancelDiscovery();
        }
    }

    private void initReceiver() {
        //注册蓝牙搜索结果、状态变化等广播接收
        mBlueReceiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //监听状态变化
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mContext.registerReceiver(mBlueReceiver, filter);
    }

    private void addBondList(BluetoothDevice device){
        if (!mBoundedDeviceList.contains(device)){
            mBoundedDeviceList.add(device);
            mBondAdapter.notifyDataSetChanged();
        }

    }

    private void removeBondList(BluetoothDevice device){
        if (mBoundedDeviceList.contains(device)){
            mBoundedDeviceList.remove(device);
            mBondAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("MissingPermission")
    private void addSearchList(BluetoothDevice device){
        if (!mSearchDeviceList.contains(device) && !TextUtils.isEmpty(device.getName())){
            mSearchDeviceList.add(device);
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    private void removeSearchList(BluetoothDevice device){
        if (mSearchDeviceList.contains(device)){
            mSearchDeviceList.remove(device);
            mSearchAdapter.notifyDataSetChanged();
        }
    }


    // 初始化蓝牙设备列表(只有已匹配过的）
    @SuppressLint("MissingPermission")
    private void initBlueDevice() {
        mBoundedDeviceList.clear();
        // 获取已经配对的蓝牙设备集合
        Log.i(TAG, "initBlueDevice: 初始化蓝牙设备列表");
         Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            Log.i(TAG, "initBlueDevice: onReceive: name="+device.getName()+",address="+device.getAddress()+",state="+device.getBondState());
            if (device.getBondState()==BOND_BONDED){
                addBondList(device);
            }else{
                addSearchList(device);

            }

        }
    }

    //注册接收广播
    private class BluetoothReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    bluetoothStateChanged(state);
                }else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    // 发现新设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "ACTION_FOUND onReceive: name="+device.getName()+",address="+device.getAddress()+",state="+device.getBondState());
                    if (device.getBondState()==BluetoothDevice.BOND_BONDED){
                        addBondList(device);
                    }else{
                        addSearchList(device);
                    }
                } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    // 设备配对状态改变
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "ACTION_BOND_STATE_CHANGED onReceive: name="+device.getName()+",address="+device.getAddress()+",state="+device.getBondState());
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    switch (bondState) {
                        case BluetoothDevice.BOND_BONDING:
                            // 设备正在配对
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            // 设备配对成功
                            // 将设备从未配对设备列表中移除
                            removeSearchList(device);
                            // 将设备添加到已配对设备列表中
                            addBondList(device);
                            break;
                        case BluetoothDevice.BOND_NONE:
                            // 设备配对失败或取消配对
                            // 将设备从已配对设备列表中移除
                            removeBondList(device);
                            // 将设备添加到未配对设备列表中
                            addSearchList(device);
                            break;
                    }
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    // 开始搜索设备
                    //初始化列表
                    initBlueDevice();
                } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    // 搜索设备完成
                } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                    // 设备已连接
                    mConnectedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 连接成功
                    tv_connectBluetooth.setText(mConnectedDevice.getName());
                } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    // 设备已断开连接
                    mConnectedDevice = null;
                    // 连接断开
                    tv_connectBluetooth.setText("无");
                }
            }
        }
    }

    private void bluetoothStateChanged(int state) {
        switch (state){
            case BluetoothAdapter.STATE_OFF:
                // 蓝牙关闭
                ck_status.setChecked(false);
                Log.i(TAG, "bluetoothStateChanged: 关闭蓝牙");
                mBlueUtil.cancelDiscovery();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                // 蓝牙正在关闭
                showTips("蓝牙正在关闭");
                clearAdapterList();
                break;
            case BluetoothAdapter.STATE_ON:
                // 蓝牙开启
                ck_status.setChecked(true);
                Log.i(TAG, "bluetoothStateChanged: 开启蓝牙");
                mBlueUtil.beginDiscovery();
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                // 蓝牙正在开启
                showTips("蓝牙正在开启");
                break;
        }
    }

    private void clearAdapterList() {
        mBoundedDeviceList.clear();
        mBondAdapter.notifyDataSetChanged();
        mSearchDeviceList.clear();
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mContext.unregisterReceiver(mBlueReceiver);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.ck_status){
            if (ClickUtil.isFastClick()){
                return;
            }
            mBlueUtil.setBlueToothStatus(isChecked);
            if(isChecked){
                //通过这个方法来请求打开我们的蓝牙设备
                //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent,BLUETOOTH_RESPONSE);
                //开始搜索
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_RESPONSE){
            if (resultCode == RESULT_OK){
                //showTips("正在打开蓝牙");
                //搜索蓝牙
            }else if (resultCode == RESULT_CANCELED){
                ck_status.setChecked(false);
                showTips("蓝牙权限获取失败，请重新打开蓝牙！");
            }
        }
    }
}
