package com.example.mobilehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.example.mobilehelper.callback.BatteryChangeListener;

public class BatteryChangeReceiver extends BroadcastReceiver {

    private BatteryChangeListener batteryChangeListener;

    public void setBatteryChangeListener(BatteryChangeListener batteryChangeListener){
        this.batteryChangeListener = batteryChangeListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            //获取当前电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //tv_battery.setText(level+"%");
            if (batteryChangeListener != null){
                batteryChangeListener.onBatteryChange(level);
            }
        }
    }
}
