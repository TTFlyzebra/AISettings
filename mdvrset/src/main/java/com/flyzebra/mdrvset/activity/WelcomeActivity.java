/**
 * FileName: WelcomeActivity
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/12/6 16:52
 * Description:
 */
package com.flyzebra.mdrvset.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.mdvrset.R;

public class WelcomeActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        mHandler.postDelayed(() -> {
            startActivity(new Intent(WelcomeActivity.this, SelectActivity.class));
            finish();
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }
}
