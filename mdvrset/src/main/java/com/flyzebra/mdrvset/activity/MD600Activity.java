package com.flyzebra.mdrvset.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.flyzebra.mdrvset.MyApp;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.ResUtil;

import java.util.ArrayList;
import java.util.List;

public class MD600Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 101;

    public String[] fmName = {"AdasSetFragment", "BsdSetFragment", "DmsSetFragment"};
    private int crt_fm = 0;

    private final int[] imageViewResID = {R.id.adas_setting_im, R.id.bsd_setting_im, R.id.dms_setting_im};
    private final int[] textViewResID = {R.id.adas_setting_tv, R.id.bsd_setting_tv, R.id.dms_setting_tv};
    private final int[] linearLayoutResID = {R.id.adas_setting_view_ll, R.id.bsd_setting_view_ll, R.id.dms_setting_view_ll};
    private final int[] imageViewSrcResID_Off = {R.drawable.ic_shezhi_normal, R.drawable.ic_shezhi_normal, R.drawable.ic_shezhi_normal};
    private final int[] imageViewSrcResID_On = {R.drawable.ic_shezhi_press, R.drawable.ic_shezhi_press, R.drawable.ic_shezhi_press};
    private final List<ImageView> iv_list = new ArrayList<>();
    private final List<TextView> tv_list = new ArrayList<>();
    private final List<LinearLayout> ll_list = new ArrayList<>();

    private ColorStateList textColer_Off;
    private int textColor_On;

    private TextView message;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcsoftset);

        for (String s : PERMISSIONS_STORAGE) {
            if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                break;
            }
        }

        textColer_Off = ResUtil.getColorStateList(this, R.color.menu_text_color);
        textColor_On = ResUtil.getColor(this, R.color.menu_text_on);

        //底部导航栏
        for (int ResId : textViewResID) {
            tv_list.add(findViewById(ResId));
        }
        for (int ResId : imageViewResID) {
            iv_list.add(findViewById(ResId));
        }
        for (int ResId : linearLayoutResID) {
            ll_list.add(findViewById(ResId));
        }
        for (LinearLayout layout : ll_list) {
            layout.setOnClickListener(this);
        }

        message = findViewById(R.id.message);

        replaceFragment(fmName[crt_fm], R.id.ac_fm01);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            FlyLog.d("onRequestPermissionsResult");
        }
    }

    public void replaceFragment(String classname, int resId) {
        try {
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            Class<?> c1 = Class.forName(MyApp.class.getPackage().getName() + ".fragment." + classname);
            Fragment fragmentRe = (Fragment) c1.newInstance();
            if (fmName[crt_fm].equals(classname)) {
                Class<?> c2 = Class.forName(MyApp.class.getPackage().getName() + ".fragment." + fmName[crt_fm]);
                Fragment fragmentRm = (Fragment) c2.newInstance();
                transaction1.remove(fragmentRm);
            }
            transaction1.replace(resId, fragmentRe, classname);
            transaction1.commit();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        int ResId = view.getId();
        for (int i = 0; i < ll_list.size(); i++) {
            if (ResId == linearLayoutResID[i]) {
                iv_list.get(i).setImageResource(imageViewSrcResID_On[i]);
                tv_list.get(i).setTextColor(textColor_On);
                replaceFragment(fmName[i], R.id.ac_fm01);
                crt_fm = i;
            } else {
                iv_list.get(i).setImageResource(imageViewSrcResID_Off[i]);
                tv_list.get(i).setTextColor(textColer_Off);
            }
        }
    }

    public void showMessage(int resId) {
        message.setText(resId);
        message.setVisibility(View.VISIBLE);
        mHandler.postDelayed(() -> {
            message.setText("");
            message.setVisibility(View.INVISIBLE);
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}