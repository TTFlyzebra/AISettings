package com.flyzebra.mdrvset.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.FlyLog;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Description:BluetoothScanner
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/11/7 9:26
 */
public class BluetoothScanner {
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter = null;
    private final BluetoothReceiver receiver = new BluetoothReceiver();
    private Hashtable<String, BluetoothGatt> gatts = new Hashtable<>();

    public BluetoothScanner(Context context) {
        mContext = context;
    }

    public void start() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mContext.registerReceiver(receiver, filter);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetoothAdapter != null) {
            try {
                if (mBluetoothAdapter.isEnabled()) {
                    startScan();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                                != PackageManager.PERMISSION_GRANTED) {
                            FlyLog.e("need Manifest.permission.BLUETOOTH_SCAN!");
                            return;
                        }
                    }
                    mContext.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                }
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }
    }

    public void stop() {
        try {
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

        stopScan();
    }

    private final ScanCallback bleScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            final BluetoothDevice device = result.getDevice();
            if (!TextUtils.isEmpty(device.getName()) && device.getName().startsWith("MD201_")) {
                device.connectGatt(mContext, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                                    != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                        }
                        if (newState == BluetoothGatt.STATE_CONNECTED) {
                            if (gatts.get(device.getAddress()) == null) {
                                gatt.discoverServices();
                                gatts.put(device.getAddress(), gatt);
                            }
                        } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                            gatt.close();
                            gatts.remove(device.getAddress());
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                                    != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                        }
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            if (gatt == null) return;
                            List<BluetoothGattService> services = gatt.getServices();
                            for (BluetoothGattService service : services) {
                                List<BluetoothGattCharacteristic> charaList = service.getCharacteristics();
                                for (BluetoothGattCharacteristic chara : charaList) {
                                    int charaProp = chara.getProperties(); // 获取该特征的属性
                                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                                        String name = device.getName().substring("MD201_".length());
                                        long imei = ByteUtil.sysIdToInt64(name);
                                        String str = imei + "";
                                        chara.setValue(str.getBytes());
                                        boolean result = gatt.writeCharacteristic(chara);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            FlyLog.e("onScanFailed");
        }
    };

    private void startScan() {
        if (mBluetoothAdapter == null) {
            FlyLog.e("BluetoothAdapter instance is null!");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                FlyLog.e("need Manifest.permission.BLUETOOTH_SCAN!");
                return;
            }
        }
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            FlyLog.d("BluetoothLeScanner startScan");
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();
            List<ScanFilter> filters = new ArrayList<>();
            try {
                bluetoothLeScanner.startScan(filters, settings, bleScanCallBack);
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }
    }

    private void stopScan() {
        if (mBluetoothAdapter == null) {
            FlyLog.e("BluetoothAdapter instance is null!");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                FlyLog.e("need Manifest.permission.BLUETOOTH_SCAN!");
                return;
            }
        }

        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            FlyLog.d("BluetoothLeScanner stopScan");
            try {
                bluetoothLeScanner.stopScan(bleScanCallBack);
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }

        Enumeration<BluetoothGatt> rtmpElement = gatts.elements();
        while (rtmpElement.hasMoreElements()) {
            BluetoothGatt gatt = rtmpElement.nextElement();
            gatt.disconnect();
            gatt.close();
        }
        gatts.clear();
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_ON) {
                        startScan();
                    }
                }
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }
    }
}
