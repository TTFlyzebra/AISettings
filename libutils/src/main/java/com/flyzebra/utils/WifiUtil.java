package com.flyzebra.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class WifiUtil {
    public static String getGateway(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String gateway = null;
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String ssid = wifiInfo.getSSID();
            String ipAddress = Formatter.formatIpAddress(wifiInfo.getIpAddress());
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            gateway = Formatter.formatIpAddress(dhcpInfo.gateway);
        }
        return gateway;
    }
}
