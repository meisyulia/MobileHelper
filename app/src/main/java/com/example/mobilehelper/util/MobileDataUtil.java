package com.example.mobilehelper.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MobileDataUtil {
    private static final String TAG = "MobileDataUtil";
    private final Context ctx;
    private final TelephonyManager tm;

    public MobileDataUtil(Context ctx) {
        this.ctx = ctx;
        // 从系统服务中获取电话管理器
        tm = (TelephonyManager)
                ctx.getSystemService(Context.TELEPHONY_SERVICE);
    }

    // 获取数据连接的开关状态
    public boolean getMobileDataStatus() {

        boolean isOpen = false;
        try {
            String methodName = "getDataEnabled"; // 这是隐藏方法，需要通过反射调用
            Method method = tm.getClass().getMethod(methodName);
            isOpen = (Boolean) method.invoke(tm);
            Log.d(TAG, "getMobileDataStatus isOpen=" + isOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;
    }

    //控制数据连接的开关
    //Android 10及以上版本限制了应用程序控制移动数据开关，只有系统应用程序才能控制移动数据开关
    public void setMobileDataEnabled(boolean enabled) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android Q及以上版本无法直接控制移动数据开关，跳转到设置页面进行操作
                    /*Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_DATA_USAGE_SETTINGS);
                    context.startActivity(intent);*/
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
                    ctx.startActivity(intent);
                    return;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Android L及以上版本的控制移动数据开关方法
                    if (Settings.System.canWrite(ctx)) {
                        // 检查是否有写入系统设置的权限
                        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                        if (tm != null) {
                            Method setMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
                            // 通过反射调用TelephonyManager的setDataEnabled方法控制移动数据开关
                            setMobileDataEnabledMethod.invoke(tm, enabled);
                        }
                    } else {
                        // 没有权限时跳转到系统设置页面请求权限
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }
                } else {
                    // Android L以下版本的控制移动数据开关方法
                    Class cmClass = Class.forName(cm.getClass().getName());
                    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                    method.setAccessible(true);
                    // 通过反射调用ConnectivityManager的getMobileDataEnabled方法获取移动数据开关状态
                    boolean isMobileDataEnabled = (Boolean) method.invoke(cm);
                    if (isMobileDataEnabled != enabled) {
                        Method setMobileDataEnabledMethod = cmClass.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                        setMobileDataEnabledMethod.setAccessible(true);
                        // 通过反射调用ConnectivityManager的setMobileDataEnabled方法控制移动数据开关
                        setMobileDataEnabledMethod.invoke(cm, enabled);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取手机卡槽数量
    public int getPhoneSlotCount() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tm.getPhoneCount();
        }
        return 0;
    }

    //获取sim卡数量
    public int getSimCount() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(ctx, "需要开启权限才能获取SIM卡数量哦", Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                int activeSubscriptionInfoCount = SubscriptionManager.from(ctx).getActiveSubscriptionInfoCount();
                return activeSubscriptionInfoCount;
            }
        }
        return 0;

    }

    public int getSignStrength() {
        int level = 0;
        //单卡
        if (getSimCount() == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                level = tm.getSignalStrength().getLevel();
            }
        } else if (getSimCount() == 2) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(ctx, "要允许权限才能获取双卡的信号强度哦", Toast.LENGTH_SHORT).show();
                return level;
            }
            List<CellInfo> allCellInfo = tm.getAllCellInfo();
            for (CellInfo cellInfo : allCellInfo) {
                if (cellInfo instanceof CellInfoGsm){
                    //GSM网络
                    level = ((CellInfoGsm) cellInfo).getCellSignalStrength().getLevel();
                }else if (cellInfo instanceof CellInfoCdma){
                    //CDMA网络
                   level =  ((CellInfoCdma) cellInfo).getCellSignalStrength().getLevel();
                }else if (cellInfo instanceof CellInfoLte){
                    //LTE网络
                    level = ((CellInfoLte)cellInfo).getCellSignalStrength().getLevel();
                }
            }
        }
        return level;
    }

    /**
     * 获取sim卡信息
     */
    private List<SubscriptionInfo> getCallingSimCard() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager mSubscriptionManager = SubscriptionManager.from(ctx);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return new ArrayList<>();
            }
            List<SubscriptionInfo> list = mSubscriptionManager.getActiveSubscriptionInfoList();
            return list;
        }
        return new ArrayList<>();
    }

    //获取卡1或卡2的信息  slotIndex:0 卡1 slotIndex:1 卡2
    public String getSimCardInfo(int slotIndex){
        String info = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int simState = tm.getSimState(slotIndex);
            if ( simState == TelephonyManager.SIM_STATE_READY){ //判断卡是否存在
                List<SubscriptionInfo> infoList = getCallingSimCard();
                for (SubscriptionInfo subscriptionInfo : infoList) {
                    if (subscriptionInfo.getSimSlotIndex() == slotIndex){
                        // 获取运营商名称
                        String operatorName = subscriptionInfo.getCarrierName().toString();
                        // 获取iccid
                        //String iccid = subscriptionInfo.getIccId();
                        String iccid = (String) getPhoneInfo(slotIndex, "getSimSerialNumber", ctx);
                        // 获取imsi
                        String imsi = (String) getPhoneInfo(slotIndex, "getSubscriberId", ctx);
                        // 获取imei
                        String imei = tm.getImei(slotIndex);
                        //String imei = (String) getPhoneInfo(slotIndex, "getDeviceId", ctx);
                        info = "\n卡"+(slotIndex+1)+"：有卡\n运营商："+operatorName+"\nICCID："+iccid+"\nIMSI："+imsi+"\nIMEI："+imei+"\n";
                    }
                }
            }else{
                info = "\n卡"+(slotIndex+1)+"：无卡\n";
            }
        }
        return info;
    }

    //1、通过方法名来获取方法的参数列表
    private Class[] getMethodParamTypes(String methodName) {
        Class[] params = null;
        try {
            Method[] methods = TelephonyManager.class.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    params = methods[i].getParameterTypes();
                    if (params.length >= 1) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return params;
    }
    //2、通过slotId和方法名来获取该方法的返回值
    public Object getPhoneInfo(int slotId, String methodName, Context context) {
        Object value = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= 21) {
                Method method = tm.getClass().getMethod(methodName, getMethodParamTypes(methodName));
                if (slotId >= 0) {
                    value = method.invoke(tm, slotId);
                }
            }
        } catch (Exception e) {
        }
        return value;
    }

    // 获取所有移动数据卡的信息
    public String getAllMobileDataInfo() {
        String allInfo = "";
        int simCount = getSimCount(); // 获取移动数据卡数量
        for (int i = 0; i < simCount; i++) {
            // 获取当前移动数据卡的信息
            String operatorName = tm.getSimOperatorName(); // 运营商名称
            String iccid = tm.getSimSerialNumber(); // ICCID
            String imsi = tm.getSubscriberId(); // IMSI
            String imei = null; // IMEI
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                imei = tm.getDeviceId(i);
            }
            int simState = 0; // 移动数据卡状态
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                simState = tm.getSimState(i);
            }

            // 判断当前移动数据卡是否可用
            if (simState == TelephonyManager.SIM_STATE_READY) {
                // do something with the available mobile data card info
                Log.d(TAG, "Mobile data card " + (i + 1) + " is available:");
                Log.d(TAG, "  Operator name: " + operatorName);
                Log.d(TAG, "  ICCID: " + iccid);
                Log.d(TAG, "  IMSI: " + imsi);
                Log.d(TAG, "  IMEI: " + imei);
                allInfo = String.format("卡%d:\n 运营商:%s \n ICCID:%s \n IMSI:%s \n IMEI:%s",
                        (i+1),operatorName,iccid,imsi,imei);
            } else {
                // do something with the unavailable mobile data card info
                Log.d(TAG, "Mobile data card " + (i + 1) + " is unavailable.");
                allInfo = "Mobile data card " + (i + 1) + " is unavailable.";
            }
        }
        return allInfo;
    }

}
