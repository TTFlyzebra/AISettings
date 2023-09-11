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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.mdvrset.R;

public class SelectActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
    }

    public void md201(View view) {
        startActivity(new Intent(this, WifiP2PSetActivity.class));
    }

    public void md600(View view) {
        startActivity(new Intent(this, AdasSetActivity.class));
    }
}
