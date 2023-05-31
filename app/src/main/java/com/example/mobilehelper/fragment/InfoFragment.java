package com.example.mobilehelper.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobilehelper.R;
import com.example.mobilehelper.callback.BatteryChangeListener;
import com.example.mobilehelper.receiver.BatteryChangeReceiver;
import com.example.mobilehelper.util.StorageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InfoFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener, BatteryChangeListener {

    private static final String TAG = "InfoFragment";
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
    private AudioManager mAudioMgr;
    private int mMaxVolume;
    private int mNowVolume;
    private int mVolumeProgress;
    private VolumeChangeReceiver volumeChangeReceiver;
    private String VOLUME_CHANGE_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    private int mNowBrightness;
    private int mMaxBrightness = 255; // 获取屏幕最大亮度
    private String mInterTotal;
    private String mSDTotal;
    private ContentObserver brightnessObserver;
    private String mInternalAvail;
    private String mSDAvail;
    private boolean mIsSafe;


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
        //获取存储总大小
        mInterTotal = StorageUtil.getExTotalSize(mContext);
        mIsSafe = StorageUtil.isSdSafe(mContext);
        if (mIsSafe){
            //mSDTotal = StorageUtil.getSDTotalSize(mContext);
            mSDTotal = StorageUtil.getSDTotalSize(mContext);
        }else{
            tv_sdSize.setText("未检测到SD卡");
        }

        // 获取屏幕亮度
        mNowBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
    }

    @Override
    protected void initView() {
        setVolumeSeek();
        sb_volume.setOnSeekBarChangeListener(this);
        setBrightness(mNowBrightness);
        sb_brightness.setOnSeekBarChangeListener(this);
    }

    private void setBrightness(int progress) {
        tv_brightness.setText(progress+"");
        sb_brightness.setMax(mMaxBrightness);
        sb_brightness.setProgress(progress);
        // 刷新当前窗口，使屏幕亮度生效
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = progress / (float) mMaxBrightness;
        window.setAttributes(layoutParams);
        mNowBrightness = progress;
    }

    private void setVolumeSeek() {
        mAudioMgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //获取最大音量
        mMaxVolume = mAudioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        mNowVolume = mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        tv_volume.setText(mNowVolume+"");
        mVolumeProgress = sb_volume.getMax() * mNowVolume / mMaxVolume;
        sb_volume.setProgress(mVolumeProgress);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 创建一个电量变化的广播接收器
        batteryChangeReceiver = new BatteryChangeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(batteryChangeReceiver, filter);
        batteryChangeReceiver.setBatteryChangeListener(this);
        //监听存储变化
        StorageUtil.startMonitoring(mContext, new StorageUtil.OnStorageChangeListener() {
            @Override
            public void onStorageChange(String internalAvailableSize, String SDAvailableSize) {
                boolean isSdSafe = StorageUtil.isSdSafe(mContext);
                mInternalAvail = internalAvailableSize;
                mSDAvail = SDAvailableSize;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_romSize.setText(mInternalAvail+"/"+mInterTotal);
                        if (isSdSafe){
                            tv_sdSize.setText(mSDAvail+"/"+mSDTotal);
                        }
                    }
                });
            }
        });
        //音量变化的广播接收器
        volumeChangeReceiver = new VolumeChangeReceiver();
        IntentFilter volFilter = new IntentFilter();
        volFilter.addAction(VOLUME_CHANGE_ACTION);
        mContext.registerReceiver(volumeChangeReceiver,volFilter);
        //监听屏幕亮度
        // 创建ContentObserver
        brightnessObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // 获取屏幕亮度
                mNowBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
                // 更新SeekBar的值
                setBrightness(mNowBrightness);
            }
        };
            // 注册ContentObserver
        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, brightnessObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(batteryChangeReceiver);
        mContext.unregisterReceiver(volumeChangeReceiver);
        mContext.getContentResolver().unregisterContentObserver(brightnessObserver);
        StorageUtil.stopMonitoring();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // 声明一个电量变化的广播接收器对象
    private BatteryChangeReceiver batteryChangeReceiver;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()){
            case R.id.sb_volume:
                int volume = seekBar.getProgress()*mMaxVolume/seekBar.getMax();
                tv_volume.setText(volume+"");
                break;
            case R.id.sb_brightness:
                setBrightness(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.sb_volume:
                int volume = seekBar.getProgress()*mMaxVolume/seekBar.getMax();
                if (volume != mNowVolume){
                    tv_volume.setText(volume+"");
                    mNowVolume = volume;
                    mVolumeProgress = sb_volume.getMax() * mNowVolume / mMaxVolume;
                    sb_volume.setProgress(mVolumeProgress);
                    // 设置该音频类型的当前音量
                    mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
                }
                break;
            case R.id.sb_brightness:
                break;
        }
    }

    @Override
    public void onBatteryChange(int level) {
        tv_battery.setText(level+"%");
    }

    /*// 定义一个电量变化的广播接收器
    private class BatteryChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                //获取当前电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                tv_battery.setText(level+"%");
            }
        }
    }*/

    private class VolumeChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int volumeType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
                if (volumeType == AudioManager.STREAM_MUSIC){
                    setVolumeSeek();
                }
            }
        }
    }
}
