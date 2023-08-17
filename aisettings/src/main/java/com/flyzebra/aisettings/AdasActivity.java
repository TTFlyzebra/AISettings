package com.flyzebra.aisettings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.ResUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdasActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 101;

    public String[] fragmentName = {"AdasFragment", "AdasFragment"};
    private int cerrent_fragment = 0;

    private final int[] imageViewResID = {R.id.adas_cali_im, R.id.adas_setting_im};
    private final int[] textViewResID = {R.id.adas_cali_tv, R.id.adas_setting_tv};
    private final int[] linearLayoutResID = {R.id.adas_view_ll, R.id.adas_setting_view_ll};
    private final int[] imageViewSrcResID_Off = {R.drawable.ic_tab_adas_normal, R.drawable.ic_shezhi_normal};
    private final int[] imageViewSrcResID_On = {R.drawable.ic_tab_adas_press, R.drawable.ic_shezhi_press};
    private final List<ImageView> iv_list = new ArrayList<>();
    private final List<TextView> tv_list = new ArrayList<>();
    private final List<LinearLayout> ll_list = new ArrayList<>();

    private ColorStateList textColer_Off;
    private int textColor_On;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            for (String s : PERMISSIONS_STORAGE) {
                if (ActivityCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                    break;
                }
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

        replaceFragment(fragmentName[cerrent_fragment], R.id.ac_fm01);
    }


    @Override
    protected void onResume() {
        super.onResume();
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
            Class<?> c1 = Class.forName(Objects.requireNonNull(AdasActivity.class.getPackage()).getName() + ".fm." + classname);
            Fragment fragmentRe = (Fragment) c1.newInstance();
            if (fragmentName[cerrent_fragment].equals(classname)) {
                Class<?> c2 = Class.forName(Objects.requireNonNull(AdasActivity.class.getPackage()).getName() + ".fm." + fragmentName[cerrent_fragment]);
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
                replaceFragment(fragmentName[i], R.id.ac_fm01);
                cerrent_fragment = i;
            } else {
                iv_list.get(i).setImageResource(imageViewSrcResID_Off[i]);
                tv_list.get(i).setTextColor(textColer_Off);
            }
        }
    }
}