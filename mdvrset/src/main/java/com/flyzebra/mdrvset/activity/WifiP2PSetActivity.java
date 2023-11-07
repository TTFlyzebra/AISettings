package com.flyzebra.mdrvset.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flyzebra.core.Fzebra;
import com.flyzebra.core.notify.INotify;
import com.flyzebra.core.notify.Notify;
import com.flyzebra.mdrvset.adapder.WifiP2PAdapter;
import com.flyzebra.mdrvset.bean.WifiP2PBean;
import com.flyzebra.mdrvset.model.WifiP2PScanner;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;

import java.util.List;

public class WifiP2PSetActivity extends AppCompatActivity implements INotify, WifiP2PScanner.IWifiP2PListener, WifiP2PAdapter.OnItemClick {
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 101;

    private WifiP2PAdapter adapter;
    private TextView message;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());
    private WifiManager wifiManager = null;
    private final WifiP2PScanner wifiP2PScanner = new WifiP2PScanner(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifip2p);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Fzebra.get().init(getApplicationContext());

        for (String s : PERMISSIONS_STORAGE) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                break;
            }
        }

        ListView listView = findViewById(R.id.ac_main_listview);
        adapter = new WifiP2PAdapter(this, listView, wifiP2PScanner.wifiP2PList, R.layout.mdvr_list_item, this);
        listView.setAdapter(adapter);

        message = findViewById(R.id.message);

        Notify.get().registerListener(this);
        wifiP2PScanner.init();
        wifiP2PScanner.addListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            FlyLog.d("onRequestPermissionsResult");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
            wifiP2PScanner.stopScan();
            wifiP2PScanner.startScan();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        wifiP2PScanner.startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wifiP2PScanner.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiP2PScanner.removeListener(this);
        wifiP2PScanner.release();
        Notify.get().unregisterListener(this);
        Fzebra.get().release();
        FlyLog.d("onDestroy");
    }

    @Override
    public void notify(byte[] data, int size) {
    }

    @Override
    public void handle(int type, byte[] data, int size, byte[] params) {
    }

    @Override
    public void notityWifiP2P(List<WifiP2PBean> list) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View v, WifiP2PBean mdvrBean) {
        if (TextUtils.isEmpty(mdvrBean.deviceIp)) {
            showMessage(R.string.wait_p2p_network);
            wifiP2PScanner.connect(mdvrBean);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.fullctl)
                .setMessage(R.string.fullmsg)
                .setNegativeButton(R.string.cancle, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNeutralButton(R.string.confirm, (dialog, which) -> {
                    Intent intent = new Intent(this, RemoteActivity.class);
                    intent.putExtra("mdvrBean", mdvrBean);
                    startActivity(intent);
                    dialog.cancel();
                })
                .show();
    }

    public void showMessage(int resId) {
        message.setText(resId);
        message.setVisibility(View.VISIBLE);
        mHandler.postDelayed(() -> {
            message.setText("");
            message.setVisibility(View.INVISIBLE);
        }, 5000);
    }
}
