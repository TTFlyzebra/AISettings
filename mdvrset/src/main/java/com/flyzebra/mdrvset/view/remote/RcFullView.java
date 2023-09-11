package com.flyzebra.mdrvset.view.remote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flyzebra.core.notify.Notify;
import com.flyzebra.core.notify.Protocol;
import com.flyzebra.utils.ByteUtil;

/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is GlVideoView
 */
public class RcFullView extends RcBaseView implements View.OnTouchListener {

    public RcFullView(Context context) {
        this(context, null);
    }

    public RcFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Fzebra.get().startScreenServer(mdvrBean.getTid());
        //Config.itemSetList.put(mdvrBean.getTid(), Config.MAX_SCREEN);
        //Notify.get().miniNotify(
        //        Protocol.SCREEN_U_READY,
        //        Protocol.SCREEN_U_READY.length,
        //        mdvrBean.getTid(),
        //        Fzebra.get().getUid(),
        //        Config.MAX_SCREEN
        //);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //Fzebra.get().stopScreenServer(mdvrBean.getTid());
        //Config.itemSetList.put(mdvrBean.getTid(), Config.MIN_SCREEN);
        //Notify.get().miniNotify(
        //        Protocol.SCREEN_U_READY,
        //        Protocol.SCREEN_U_READY.length,
        //        mdvrBean.getTid(),
        //        Fzebra.get().getUid(),
        //        Config.MIN_SCREEN
        //);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP: {
                int pcount = event.getPointerCount();
                byte[] data = new byte[48];
                data[0] = (byte) (event.getAction() >> 8 & 0xFF);
                data[1] = (byte) (event.getAction() & 0xFF);
                data[2] = (byte) pcount;
                data[3] = (byte) 0x00;
                data[4] = (byte) (mWidth >> 8 & 0xFF);
                data[5] = (byte) (mWidth & 0xFF);
                data[6] = (byte) (mHeight >> 8 & 0xFF);
                data[7] = (byte) (mHeight & 0xFF);
                long time = event.getEventTime();
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    time -= 10;
                } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    time += 10;
                }
                ByteUtil.longToBytes(time, data, 8, false);
                for (int i = 0; i < pcount; i++) {
                    data[16 + i * 4] = (byte) ((short) event.getX(i) >> 8 & 0xFF);
                    data[17 + i * 4] = (byte) ((short) event.getX(i) & 0xFF);
                    data[18 + i * 4] = (byte) ((short) event.getY(i) >> 8 & 0xFF);
                    data[19 + i * 4] = (byte) ((short) event.getY(i) & 0xFF);
                }
                Notify.get().miniNotify(
                        Protocol.INPUT_TOUCH_SINGLE,
                        Protocol.INPUT_TOUCH_SINGLE.length,
                        mdvrBean.getTid(),
                        0,
                        data
                );
                break;
            }
        }
        return true;
    }
}
