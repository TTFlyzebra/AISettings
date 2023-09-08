/**
 * FileName: PhoneActivity
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/11/28 10:28
 * Description:
 */
package com.flyzebra.mdrvset.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flyzebra.core.Fzebra;
import com.flyzebra.core.notify.INotify;
import com.flyzebra.core.notify.Notify;
import com.flyzebra.core.notify.Protocol;
import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdrvset.view.mdvrview.MdvrFullView;
import com.flyzebra.mdrvset.wifip2p.MdvrBean;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.DisplayUtil;
import com.flyzebra.utils.SPUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class MdvrFullActivity extends Activity implements View.OnClickListener, INotify {
    private MdvrFullView mdvrView;
    private ImageView fButton;
    private LinearLayout fMenu;
    private Button sysreboot;
    private Button screenDpi;
    private Button powerkey;
    private Button voladd;
    private Button voldel;
    private Button backkey;
    private Button homekey;
    private Button exitfull;
    private final float mAlpha = 0.5f;
    private final long mHideTime = 3000;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int screen_w;
    private int screen_h;

    private static final HandlerThread mCmdThread = new HandlerThread("phone_cmd");

    static {
        mCmdThread.start();
    }

    private final Handler mCmdHandler = new Handler(mCmdThread.getLooper());
    private MdvrBean mdvrBean = null;
    private AtomicBoolean isConnect = new AtomicBoolean(true);
    private AtomicBoolean isStop = new AtomicBoolean(true);
    private long lastConnectTime = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdvr);

        screen_w = DisplayUtil.getMetrices(this).widthPixels;
        screen_h = DisplayUtil.getMetrices(this).heightPixels;

        mdvrView = findViewById(R.id.ac_phoneview);
        Intent intent = getIntent();

        mdvrBean = intent.getParcelableExtra("mdvrBean");
        mdvrView.setMdvrBean(mdvrBean);

        fButton = findViewById(R.id.ac_phone_fbutton);
        fMenu = findViewById(R.id.ac_phone_menu);
        sysreboot = findViewById(R.id.fm_phone_sysreboot);
        screenDpi = findViewById(R.id.fm_phone_screendpi);
        powerkey = findViewById(R.id.fm_phone_powerkey);
        voladd = findViewById(R.id.fm_phone_voladd);
        voldel = findViewById(R.id.fm_phone_voldel);
        backkey = findViewById(R.id.fm_phone_backkey);
        homekey = findViewById(R.id.fm_phone_homekey);
        exitfull = findViewById(R.id.fm_phone_exitfull);

        sysreboot.setOnClickListener(this);
        screenDpi.setOnClickListener(this);
        powerkey.setOnClickListener(this);
        voladd.setOnClickListener(this);
        voldel.setOnClickListener(this);
        backkey.setOnClickListener(this);
        homekey.setOnClickListener(this);
        exitfull.setOnClickListener(this);

        fButton.setAlpha(mAlpha);
        fButton.setOnClickListener(this);
        fButton.setOnTouchListener(new View.OnTouchListener() {
            private int bLeft;
            private int bTop;
            private int bWidth;
            private int bHeight;
            private int rawX;
            private int rawY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        fButton.setAlpha(1.0f);
                        bLeft = fButton.getLeft();
                        bTop = fButton.getTop();
                        bWidth = fButton.getMeasuredWidth();
                        bHeight = fButton.getMeasuredHeight();
                        rawX = (int) event.getRawX();
                        rawY = (int) event.getRawY();
                        return false;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int moveX = (int) event.getRawX() - rawX;
                        int moveY = (int) event.getRawY() - rawY;
                        layoutFloatButton(bLeft + moveX, bTop + moveY, bLeft + moveX + bWidth, bTop + moveY + bHeight);
                        return false;
                    }
                    case MotionEvent.ACTION_UP: {
                        fButton.setAlpha(mAlpha);
                        int left = fButton.getLeft();
                        int top = fButton.getTop();
                        int abs = (bWidth) / 2;
                        if (Math.abs(left - bLeft) > abs || Math.abs(top - bTop) > abs) {
                            SPUtil.set(MdvrFullActivity.this, "bfLeft", left);
                            SPUtil.set(MdvrFullActivity.this, "bfTop", top);
                            return true;
                        } else {
                            layoutFloatButton(bLeft, bTop, bLeft + bWidth, bTop + bHeight);
                            return false;
                        }
                    }
                }
                return false;
            }
        });

        int left = (int) SPUtil.get(MdvrFullActivity.this, "bfLeft", 0);
        int top = (int) SPUtil.get(MdvrFullActivity.this, "bfTop", screen_h / 2);
        if (left != 0 || top != 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fButton.getLayoutParams();
            params.leftMargin = left;
            params.topMargin = top;
            fButton.setLayoutParams(params);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCmdHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Notify.get().registerListener(this);
        isStop.set(false);
        lastConnectTime = SystemClock.uptimeMillis();
        new Thread(() -> {
            while (!isStop.get()) {
                if (SystemClock.uptimeMillis() - lastConnectTime > 5000) {
                    isConnect.set(false);
                    runOnUiThread(() -> fMenu.setVisibility(View.VISIBLE));
                    mdvrView.showDisconnect();
                }
                try {
                    for (int i = 0; i < 10; i++) {
                        if (isStop.get()) return;
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop.set(true);
        Notify.get().unregisterListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            if ((fMenu.getVisibility() == View.VISIBLE)) {
                fMenu.setVisibility(View.INVISIBLE);
                fButton.setVisibility(View.VISIBLE);
            }
        }
        showFloatButton(mHideTime);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        showFloatButton(mHideTime);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        sendInputKey(KeyEvent.KEYCODE_BACK);
    }

    private void showFloatButton(long delayMillis) {
        if (!(fMenu.getVisibility() == View.VISIBLE)) {
            fButton.setVisibility(View.VISIBLE);
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(() -> {
            if (isConnect.get()) {
                fButton.setVisibility(View.INVISIBLE);
            }
        }, delayMillis);
    }

    private void layoutFloatButton(int left, int top, int right, int bottom) {
        if (left < 0) {
            left = 0;
            right = left + fButton.getMeasuredWidth();
        }
        if (top < 0) {
            top = 0;
            bottom = top + fButton.getMeasuredHeight();
        }
        if (right > screen_w) {
            right = screen_w;
            left = screen_w - fButton.getMeasuredWidth();
        }
        if (bottom > screen_h) {
            bottom = screen_h;
            top = screen_h - fButton.getMeasuredHeight();
        }
        fButton.layout(left, top, right, bottom);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.ac_phone_fbutton) {
            fButton.setVisibility(View.INVISIBLE);
            fMenu.setVisibility(View.VISIBLE);
            showFloatButton(mHideTime);
        } else if (resId == R.id.fm_phone_screendpi) {
            final byte[] data = {
                    (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x16,
                    (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };
            Notify.get().miniNotify(
                    Protocol.SCREEN_U_READY,
                    Protocol.SCREEN_U_READY.length,
                    mdvrBean.getTid(),
                    Fzebra.get().getUid(),
                    data
            );
        } else if (resId == R.id.fm_phone_sysreboot) {
            Notify.get().miniNotify(
                    Protocol.SYSTEM_REBOOT,
                    Protocol.SYSTEM_REBOOT.length,
                    mdvrBean.getTid(),
                    0,
                    null
            );
            finish();
        } else if (resId == R.id.fm_phone_powerkey) {
            sendInputKey(KeyEvent.KEYCODE_POWER);
        } else if (resId == R.id.fm_phone_voladd) {
            sendInputKey(KeyEvent.KEYCODE_VOLUME_UP);
        } else if (resId == R.id.fm_phone_voldel) {
            sendInputKey(KeyEvent.KEYCODE_VOLUME_DOWN);
        } else if (resId == R.id.fm_phone_backkey) {
            sendInputKey(KeyEvent.KEYCODE_BACK);
        } else if (resId == R.id.fm_phone_homekey) {
            sendInputKey(KeyEvent.KEYCODE_HOME);
        } else if (resId == R.id.fm_phone_exitfull) {
            finish();
        }
    }

    public void sendInputKey(int key) {
        byte[] data = {0x00, 0x00, (byte) (key >> 8 & 0xFF), (byte) (key & 0xFF), 0x00, 0x00, 0x00, 0x00};
        Notify.get().miniNotify(
                Protocol.INPUT_KEY_SINGLE,
                Protocol.INPUT_KEY_SINGLE.length,
                mdvrBean.getTid(),
                0,
                data
        );
    }

    @Override
    public void notify(byte[] data, int size) {
        long tid = ByteUtil.bytes2Long(data, 8, true);
        if (tid != mdvrBean.getTid()) return;
        short type = ByteUtil.bytes2Short(data, 2, true);
        switch (type) {
            case Protocol.TYPE_TU_HEARTBEAT:
            case Protocol.TYPE_T_CONNECTED:
            case Protocol.TYPE_T_DISCONNECTED:
            case Protocol.TYPE_U_CONNECTED:
            case Protocol.TYPE_U_DISCONNECTED:
                mCmdHandler.post(() -> handleCmd(data, size));
                break;
        }
    }

    @Override
    public void handle(int type, byte[] data, int size, byte[] params) {

    }

    private void handleCmd(final byte[] data, final int size) {
        short type = ByteUtil.bytes2Short(data, 2, true);
        switch (type) {
            case Protocol.TYPE_TU_HEARTBEAT:
                if (!isConnect.get()) {
                    isConnect.set(true);
                    resetctl(false);
                    runOnUiThread(() -> showFloatButton(mHideTime));
                }
                lastConnectTime = SystemClock.uptimeMillis();
                break;
            case Protocol.TYPE_T_CONNECTED:
            case Protocol.TYPE_U_CONNECTED:
                isConnect.set(true);
                resetctl(false);
                runOnUiThread(() -> showFloatButton(mHideTime));
                break;
            case Protocol.TYPE_T_DISCONNECTED:
            case Protocol.TYPE_U_DISCONNECTED:
                isConnect.set(false);
                break;
        }
    }

    public void resetctl(boolean resetAll) {
        if (resetAll) {
            byte[] set_screen = Config.itemSetList.get(mdvrBean.getTid());
            Notify.get().miniNotify(
                    Protocol.SCREEN_U_READY,
                    Protocol.SCREEN_U_READY.length,
                    mdvrBean.getTid(),
                    Fzebra.get().getUid(),
                    set_screen
            );
        }
    }
}
