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

public class OptionActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
    }

    public void md201(View view) {
        startActivity(new Intent(this, MD201Activity.class));
    }

    public void md600(View view) {
        startActivity(new Intent(this, MD600Activity.class));
    }
}
