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
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.flyzebra.utils.FlyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiP2PScanner {
    public final List<MdvrBean> wifiP2PList = new ArrayList<>();
    private final Context mContext;
    private WifiP2pManager wifiP2pManager;
    private Channel wifChannel;
    private MyRecevier myRecevier;
    private final AtomicBoolean is_stop = new AtomicBoolean(true);

    public WifiP2PScanner(Context context) {
        mContext = context;
    }

    public void start() {
        wifiP2PList.clear();
        wifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        wifChannel = wifiP2pManager.initialize(mContext, getMainLooper(), () -> {
            FlyLog.d("wifip2p channel disconnected!");
        });
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
                FlyLog.d("discoverPeers success!");
            }

            @Override
            public void onFailure(int i) {
                FlyLog.e("discoverPeers failure %d!", i);
            }
        });
    }

    public void stop() {
        mContext.unregisterReceiver(myRecevier);
        wifiP2pManager.stopPeerDiscovery(wifChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                FlyLog.d("stopPeerDiscovery success!");
            }

            @Override
            public void onFailure(int i) {
                FlyLog.e("stopPeerDiscovery failure %d!", i);
            }
        });
        wifiP2pManager.cancelConnect(wifChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                FlyLog.d("cancelConnect success!");
            }

            @Override
            public void onFailure(int i) {
                FlyLog.w("cancelConnect failure %d!", i);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            wifChannel.close();
        }
    }

    private class MyRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                //int wifi_p2p_state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                //if (wifi_p2p_state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //    FlyLog.d("wiifp2p is enabled!");
                //} else if (wifi_p2p_state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                //    FlyLog.e("wiifp2p is disabled!");
                //}
            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                //int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
                //if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                //    FlyLog.d("wifip2p discovery started!");
                //} else if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                //    FlyLog.d("wifip2p discovery stopped!");
                //}
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                WifiP2pDeviceList wifiP2pDeviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    if (device.deviceName.startsWith("MD201_")) {
                        boolean is_added = false;
                        for (MdvrBean wifiP2PBean : wifiP2PList) {
                            if (wifiP2PBean.deviceName.equals(device.deviceName)) {
                                is_added = true;
                                break;
                            }
                        }
                        if (!is_added) {
                            final MdvrBean wifiP2PBean = new MdvrBean();
                            wifiP2PBean.deviceName = device.deviceName;
                            wifiP2PBean.deviceAddress = device.deviceAddress;
                            wifiP2PList.add(wifiP2PBean);
                            FlyLog.d("added new device: %s-%s", device.deviceName, device.deviceAddress);
                            for (IWifiP2PListener listener : listeners) {
                                listener.notityWifiP2P(wifiP2PList);
                            }
                        }
                    }
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                    if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                        WifiP2pGroup p2pGroupInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                        WifiP2pDevice device = p2pGroupInfo.getOwner();
                        for (MdvrBean wifiP2PBean : wifiP2PList) {
                            if (wifiP2PBean.deviceName.equals(device.deviceName) && wifiP2PBean.deviceAddress.equals(device.deviceAddress)) {
                                wifiP2PBean.deviceIp = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                            }
                        }
                        for (IWifiP2PListener listener : listeners) {
                            listener.notityWifiP2P(wifiP2PList);
                        }
                    }
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                //WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            }
        }
    }

    public void connect(MdvrBean wifiP2PBean) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2PBean.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        wifiP2pManager.cancelConnect(wifChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                FlyLog.d("cancelConnect success!");
            }

            @Override
            public void onFailure(int i) {
                FlyLog.w("cancelConnect failure %d!", i);
            }
        });

        wifiP2pManager.connect(wifChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                FlyLog.d("Connect to %s success", wifiP2PBean.deviceName);
            }

            @Override
            public void onFailure(int i) {
                FlyLog.e("Connect to %s failed %d", wifiP2PBean.deviceName, i);
            }
        });
    }

    public interface IWifiP2PListener {
        void notityWifiP2P(List<MdvrBean> list);
    }

    private List<IWifiP2PListener> listeners = new ArrayList<>();

    public void addWifiP2PListener(IWifiP2PListener listener) {
        listeners.add(listener);
    }

    public void removeWifiP2PListener(IWifiP2PListener listener) {
        listeners.remove(listener);
    }
}
