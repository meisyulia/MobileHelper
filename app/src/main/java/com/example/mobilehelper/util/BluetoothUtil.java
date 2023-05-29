package com.example.mobilehelper.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.Set;

public class BluetoothUtil {

    private final Context ctx;
    private BluetoothAdapter mBluetooth;

    public BluetoothUtil(Context ctx) {
        this.ctx = ctx;
        mBluetooth = initBluetooth();
    }

    //初始化蓝牙适配器
    @Nullable
    public BluetoothAdapter initBluetooth() {
        BluetoothAdapter mBluetooth;
        // Android从4.3开始增加支持BLE技术（即蓝牙4.0及以上版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 从系统服务中获取蓝牙管理器
            BluetoothManager bm = (BluetoothManager)
                    ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetooth = bm.getAdapter();
        } else {
            // 获取系统默认的蓝牙适配器
            mBluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetooth == null) {
            Toast.makeText(ctx, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
        }
        return mBluetooth;
    }

    // 获取蓝牙的开关状态
    public boolean getBlueToothStatus() {
        boolean enabled;
        switch (mBluetooth.getState()) {
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_TURNING_ON:
                enabled = true;
                break;
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
            default:
                enabled = false;
                break;
        }
        return enabled;
    }

    // 打开或关闭蓝牙
    public void setBlueToothStatus(boolean enabled) {
        if (enabled) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //Toast.makeText(ctx, "要允许权限才能正常使用哦", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!getBlueToothStatus()) {
                mBluetooth.enable();
            }
        } else {
            if (getBlueToothStatus()) {
                mBluetooth.disable();
            }
        }
    }

    // 开始扫描周围的蓝牙设备
    @SuppressLint("MissingPermission")
    public void beginDiscovery() {
        // 如果当前不是正在搜索，则开始新的搜索任务
        if (!mBluetooth.isDiscovering()) {
            //initBlueDevice(); // 初始化蓝牙设备列表
            mBluetooth.startDiscovery(); // 开始扫描周围的蓝牙设备
        }
    }

    // 取消蓝牙设备的搜索
    @SuppressLint("MissingPermission")
    public void cancelDiscovery() {
        // 当前正在搜索，则取消搜索任务
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery(); // 取消扫描周围的蓝牙设备
        }
    }


}
