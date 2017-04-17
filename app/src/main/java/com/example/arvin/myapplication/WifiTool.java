package com.example.arvin.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvin on 2017/4/11.
 */
public class WifiTool {
    // 上下文Context对象
    private Context mContext;
    // WifiManager对象
    private WifiManager mWifiManager;

    public WifiTool(Context mContext) {
        this.mContext = mContext;
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 判断手机是否连接在Wifi上
     */
    public boolean isConnectWifi() {
        // 获取ConnectivityManager对象
        ConnectivityManager conMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo info = conMgr.getActiveNetworkInfo();
        // 获取连接的方式为wifi
        NetworkInfo.State wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();

        if (info != null && info.isAvailable() && wifi == NetworkInfo.State.CONNECTED)

        {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 获取当前手机所连接的wifi信息
     */
    public WifiInfo getCurrentWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    /**
     * 添加一个网络并连接
     * 传入参数：WIFI发生配置类WifiConfiguration
     */
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        return mWifiManager.enableNetwork(wcgID, true);
    }

    /**
     * 搜索附近的热点信息，并返回所有热点为信息的SSID集合数据
     */
    public List<ScanResult> getScanSSIDsResult() {
        // 扫描的热点数据
        List<ScanResult> resultList;
        // 开始扫描热点
        mWifiManager.startScan();
        resultList = mWifiManager.getScanResults();

        return resultList;
    }

    /**
     * 开启wifi 热点
     *
     * @param ssid
     * @param pwd
     * @param hiddenSSID 是否隐藏SSID
     */
    public void startAp(String ssid, String pwd, boolean hiddenSSID) {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        wifiManager.getDhcpInfo();
        Method method1 = null;
        try {
            method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = ssid;
            netConfig.preSharedKey = pwd;
            netConfig.hiddenSSID = hiddenSSID;
            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            method1.invoke(wifiManager, netConfig, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 关闭热点
     */
    public void stopAp() {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        Method method1 = null;
        try {
            method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method1.invoke(wifiManager, null, false);
//            if (!wifiManager.isWifiEnabled()) {
//                wifiManager.setWifiEnabled(true);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 打开wifi
     */
    public void openWifi() {
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi(){
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 关闭wifi
     */
    public boolean isWifiOpen(){
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

}



