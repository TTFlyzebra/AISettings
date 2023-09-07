package com.flyzebra.mdrvset;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flyzebra.core.Fzebra;
import com.flyzebra.core.notify.INotify;
import com.flyzebra.core.notify.Notify;
import com.flyzebra.mdrvset.activity.MdvrActivity;
import com.flyzebra.mdrvset.adapder.MdvrAdapter;
import com.flyzebra.mdrvset.wifip2p.MdvrBean;
import com.flyzebra.mdrvset.wifip2p.WifiP2PScanner;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;

import java.util.List;

public class MainActivity extends AppCompatActivity implements INotify, WifiP2PScanner.IWifiP2PListener, MdvrAdapter.OnItemClick {
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 101;

    private ListView listView;
    private MdvrAdapter adapter;

    private WifiP2PScanner wifiP2PServer = new WifiP2PScanner(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.ac_main_listview);
        adapter = new MdvrAdapter(this, listView, wifiP2PServer.wifiP2PList, R.layout.mdvr_list_item, this);
        listView.setAdapter(adapter);

        for (String s : PERMISSIONS_STORAGE) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                break;
            }
        }
        Notify.get().registerListener(this);

        wifiP2PServer.addWifiP2PListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            FlyLog.d("onRequestPermissionsResult");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        wifiP2PServer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wifiP2PServer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void notityWifiP2P(List<MdvrBean> list) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View v, MdvrBean mdvrBean) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.fullctl)
                .setMessage(R.string.fullmsg)
                .setNegativeButton(R.string.cancle, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNeutralButton(R.string.confirm, (dialog, which) -> {
                    Intent intent = new Intent(this, MdvrActivity.class);
                    intent.putExtra("MDVR", mdvrBean);
                    startActivity(intent);
                    dialog.cancel();
                })
                .show();
    }
}
