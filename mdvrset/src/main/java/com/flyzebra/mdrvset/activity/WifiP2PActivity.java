package com.flyzebra.mdrvset.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flyzebra.mdrvset.wifip2p.WifiP2PServer;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;

public class WifiP2PActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 101;

    private WifiP2PServer wifiP2PServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifip2p);

        for (String s : PERMISSIONS_STORAGE) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                break;
            }
        }

        wifiP2PServer = new WifiP2PServer(this);
        wifiP2PServer.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            FlyLog.d("onRequestPermissionsResult");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiP2PServer.stop();
    }
}
