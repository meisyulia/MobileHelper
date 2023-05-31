package com.example.mobilehelper.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkSpeedTestUtil {

    private static final String TAG = "NetworkSpeedTestUtil";
    private final Context mContext;
    //private String mDownloadUrl = "http://ipv4.download.thinkbroadband.com/1GB.zip"; // 测试下载速度的url
    //private String mDownloadUrl = "http://download.qiniup.com/test"; // 测试下载速度的url
    //private String mDownloadUrl = "https://res06.bignox.com/full/20230330/e06d3b5f11c04d2cabe19ab695184552.exe?filename=nox_setup_v7.0.5.6_full.exe"; // 测试下载速度的url
    private String mDownloadUrl = "https://jp.testmy.net/b/download";
    //private String mUploadUrl = "https://upload.qiniup.com/test";
    //private String mUploadUrl = "http://speedtest.tele2.net/upload.php";  //
    //private String mUploadUrl = "http://speed.139site.com/";
    private String mUploadUrl = "https://testmy.net/upload";
    private int mFileSize = 1048576; // 测试下载或上传速度的文件大小 1M
    private int mTestInterval = 1000; // 测试间隔 1秒

    public NetworkSpeedTestUtil(Context context){
        mContext = context;
    }

    //判断当前网络是否可用
    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //计算上传速度
    public float getUploadSpeed(){
        int bytesSent = 0;
        long startTime = System.currentTimeMillis();
        try {
            URL uploadUrl = new URL(mUploadUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) uploadUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(5000); // 连接超时时间为5秒
            urlConnection.setReadTimeout(5000); // 读取数据超时时间为5秒
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(mFileSize));
            urlConnection.connect();
            OutputStream outputStream = urlConnection.getOutputStream();
            byte[] buffer = new byte[mFileSize];
            // 按照指定大小循环写入输出流
            while (bytesSent < mFileSize) {
                outputStream.write(buffer);
                bytesSent += mFileSize;
                long currentTime = System.currentTimeMillis();
                // 计算当前时间距离开始时间的时间差
                long timeInterval = currentTime - startTime;
                if (timeInterval >= mTestInterval) {
                    // 间隔时间内发送的数据量除以用时秒数即为上传速度
                    float speed = (bytesSent / 1024f) / (timeInterval / 1000f);
                    return speed;
                }
            }
            outputStream.flush();
            outputStream.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //计算下载速度
    public float getDownloadSpeed(){
        int bytesRead = 0;
        long startTime = System.currentTimeMillis();
        try {
            URL url = new URL(mDownloadUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            Log.i(TAG, "getDownloadSpeed: responseCode="+responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK){
                //获取输入流
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[mFileSize];
                int len;
                long bytesDownloaded = 0;
                //循环读取输入流中的数据
                while ((len = inputStream.read(buffer))!= -1){
                    bytesRead +=len;
                    bytesDownloaded +=len;
                    long currentTime = System.currentTimeMillis();
                    //计算时间差
                    long timeInterval = currentTime - startTime;
                    if (timeInterval>=mTestInterval){
                        float speed = (bytesDownloaded / 1024f) / (timeInterval / 1000f);
                        Log.i(TAG, "getDownloadSpeed: speed="+speed);
                        return speed;
                    }
                }
                inputStream.close();
            }
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
