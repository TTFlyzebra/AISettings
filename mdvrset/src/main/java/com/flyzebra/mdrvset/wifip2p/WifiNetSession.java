package com.flyzebra.mdrvset.wifip2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class WifiNetSession {
    private Context mContext;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifChannel;
    public WifiP2pDevice device;
    private AtomicBoolean is_stop = new AtomicBoolean(true);

    public WifiNetSession(Context context, WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDevice device) {
        mContext = context;
        this.device = device;
        wifiP2pManager = manager;
        wifChannel = channel;
    }

    public void start() {

    }

    public void stop() {
        is_stop.set(true);
    }
}
