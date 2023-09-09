package com.flyzebra.mdrvset.view.mdvrview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.flyzebra.core.Fzebra;
import com.flyzebra.core.notify.Notify;
import com.flyzebra.core.notify.Protocol;
import com.flyzebra.mdrvset.Config;
import com.flyzebra.mdrvset.activity.MdvrFullActivity;
import com.flyzebra.mdrvset.wifip2p.MdvrBean;
import com.flyzebra.mdvrset.R;
import com.flyzebra.utils.ByteUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is GlVideoView
 */
public class MdvrItemView extends MdvrBaseView {
    private Handler mHander = new Handler(Looper.getMainLooper());
    private Runnable selfFixedThread = new Runnable() {
        @Override
        public void run() {
            Notify.get().miniNotify(
                    Protocol.UT_HEARTBEAT,
                    Protocol.UT_HEARTBEAT.length,
                    mdvrBean.getTid(),
                    Fzebra.get().getUid(),
                    null
            );
            mHander.postDelayed(selfFixedThread, 1000);
        }
    };

    private static final HandlerThread mCmdThread = new HandlerThread("phone_cmd");

    static {
        mCmdThread.start();
    }

    private final Handler mCmdHandler = new Handler(mCmdThread.getLooper());

    private AtomicBoolean isConnect = new AtomicBoolean(true);
    private AtomicBoolean isStop = new AtomicBoolean(true);
    private long lastConnectTime = 0;

    public MdvrItemView(Context context) {
        this(context, null);
    }

    public MdvrItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    public void setMdvrBean(MdvrBean mdvrBean) {
        super.setMdvrBean(mdvrBean);
        if (!TextUtils.isEmpty(mdvrBean.deviceIp)) {
            start();
        }
    }

    private void start() {
        if (TextUtils.isEmpty(mdvrBean.deviceIp) || !isStop.get()) return;
        isStop.set(false);
        Fzebra.get().startUserlSession(Fzebra.get().getUid(), mdvrBean.getTid(), mdvrBean.deviceIp);
        mHander.postDelayed(selfFixedThread, 0);
        Fzebra.get().startScreenServer(mdvrBean.getTid());
        Config.itemSetList.put(mdvrBean.getTid(), Config.MAX_SCREEN);
        Notify.get().miniNotify(
                Protocol.SCREEN_U_READY,
                Protocol.SCREEN_U_READY.length,
                mdvrBean.getTid(),
                Fzebra.get().getUid(),
                Config.MAX_SCREEN
        );

        new Thread(() -> {
            while (!isStop.get()) {
                if (SystemClock.uptimeMillis() - lastConnectTime > 5000) {
                    isConnect.set(false);
                    showDisconnect();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stop() {
        if (isStop.get()) return;
        isStop.set(true);
        Fzebra.get().stopScreenServer(mdvrBean.getTid());
        mHander.removeCallbacksAndMessages(null);
        Notify.get().miniNotify(
                Protocol.SCREEN_U_STOP,
                Protocol.SCREEN_U_STOP.length,
                mdvrBean.getTid(),
                Fzebra.get().getUid(),
                null
        );
        Config.itemSetList.remove(mdvrBean.getTid());
        if (!TextUtils.isEmpty(mdvrBean.deviceIp)) {
            Fzebra.get().stopUserSession(mdvrBean.deviceIp);
        }
    }

    public void gotoFullView() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.fullctl)
                .setMessage(R.string.fullmsg)
                .setNegativeButton(R.string.cancle, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNeutralButton(R.string.confirm, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), MdvrFullActivity.class);
                    intent.putExtra("PHONE", mdvrBean);
                    getContext().startActivity(intent);
                    dialog.cancel();
                })
                .show();
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

    private void handleCmd(final byte[] data, final int size) {
        short type = ByteUtil.bytes2Short(data, 2, true);
        switch (type) {
            case Protocol.TYPE_TU_HEARTBEAT:
                if (!isConnect.get()) {
                    isConnect.set(true);
                    resetctl();
                }
                lastConnectTime = SystemClock.uptimeMillis();
                break;
            case Protocol.TYPE_T_CONNECTED:
            case Protocol.TYPE_U_CONNECTED:
                isConnect.set(true);
                resetctl();
                break;
            case Protocol.TYPE_T_DISCONNECTED:
            case Protocol.TYPE_U_DISCONNECTED:
                isConnect.set(false);
                break;
        }
    }

    private void resetctl() {
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
