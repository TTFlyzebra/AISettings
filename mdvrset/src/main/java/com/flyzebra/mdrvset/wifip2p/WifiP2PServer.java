package com.flyzebra.mdrvset.wifip2p;

import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import androidx.core.app.ActivityCompat;

import com.flyzebra.utils.FlyLog;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiP2PServer {
    private final Context mContext;
    private WifiP2pManager wifiP2pManager;
    private Channel wifChannel;
    private MyRecevier myRecevier;
    private final AtomicBoolean is_stop = new AtomicBoolean(true);
    private final List<WifiNetSession> sessionList = new ArrayList<>();

    public WifiP2PServer(Context context) {
        mContext = context;
    }

    public void start() {
        is_stop.set(false);
        wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        wifChannel = wifiP2pManager.initialize(mContext, getMainLooper(), null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        myRecevier = new MyRecevier();
        mContext.registerReceiver(myRecevier, intentFilter);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            FlyLog.e("checkSelfPermission ACCESS_FINE_LOCATION failed!");
            return;
        }

        wifiP2pManager.discoverPeers(wifChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i) {
            }
        });
    }

    public void stop() {
        is_stop.set(true);
        mContext.unregisterReceiver(myRecevier);
        for (WifiNetSession session : sessionList) {
            session.stop();
        }
        sessionList.clear();
    }

    private class MyRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Wi-Fi Direct 状态发生变化
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    FlyLog.d("Wi-Fi Direct 已启用");
                } else {
                    FlyLog.d("Wi-Fi Direct 已停用");
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                wifiP2pManager.requestPeers(wifChannel, peerList -> {
                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        if (device.deviceName.startsWith("MD201_")) {
                            boolean is_added = false;
                            for (WifiNetSession session : sessionList) {
                                if (session.device.deviceName.equals(device.deviceName)) {
                                    is_added = true;
                                    break;
                                }
                            }
                            if (!is_added) {
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                is_stop.set(false);

                                WifiP2pConfig config = new WifiP2pConfig();
                                config.deviceAddress = device.deviceAddress;
                                config.wps.setup = WpsInfo.PBC;
                                FlyLog.d("Connect to %s", device.deviceName);
                                wifiP2pManager.connect(wifChannel, config, null);
                                FlyLog.d("added new device: %s-%s", device.deviceName, device.deviceAddress);
                                WifiNetSession session = new WifiNetSession(mContext, wifiP2pManager, wifChannel, device);
                                sessionList.add(session);
                            }
                        }
                    }
                });
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    wifiP2pManager.requestConnectionInfo(wifChannel, wifiP2pInfo -> {
                        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                            Socket socket = new Socket();
                            try {
                                socket.connect(new InetSocketAddress(wifiP2pInfo.groupOwnerAddress, 8888), 5000);
                                // 向服务端发送数据
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                writer.write("Hello, World!");
                                writer.newLine();
                                writer.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        FlyLog.e("wifiP2pManager onConnectionInfoAvailable " + wifiP2pInfo);
                    });
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            }
        }
    }
}
