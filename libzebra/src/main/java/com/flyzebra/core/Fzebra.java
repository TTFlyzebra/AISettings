package com.flyzebra.core;

import android.content.Context;

import com.flyzebra.core.notify.INotify;
import com.flyzebra.core.notify.Notify;
import com.flyzebra.core.notify.Protocol;
import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.IDUtil;

public class Fzebra implements INotify {
    static {
        System.loadLibrary("zebra");
    }

    private Context mContext;
    private long _ptr_obj = -1;
    private long mTid = 0;
    private static long mUid = (long) (Math.random() * Long.MAX_VALUE);

    private Fzebra() {
    }

    private static class ZebraNativeHolder {
        public static final Fzebra sInstance = new Fzebra();
    }

    public static Fzebra get() {
        return Fzebra.ZebraNativeHolder.sInstance;
    }

    public void init(Context context) {
        mContext = context;
        _ptr_obj = _init();
        try {
            String imei = IDUtil.getIMEI(mContext);
            mTid = Long.parseLong(imei);
            _setTid(mTid);
            _setUid(mUid);
        } catch (Exception e) {
            FlyLog.e();
        }
        Notify.get().registerListener(this);
    }

    public void release() {
        Notify.get().unregisterListener(this);
        if (_ptr_obj < 0) return;
        long pointer = _ptr_obj;
        _ptr_obj = -1;
        _release(pointer);
    }

    public long getTid() {
        return mTid;
    }

    public long getUid() {
        return mUid;
    }

    @Override
    public void notify(byte[] data, int size) {
        if (_ptr_obj < 0) return;
        short type = ByteUtil.bytes2Short(data, 2, true);
        switch (type) {
            case Protocol.TYPE_UT_HEARTBEAT:
            case Protocol.TYPE_U_LOGIN:
            case Protocol.TYPE_USER_TID_ADD:
            case Protocol.TYPE_USER_TID_REMOVE:
            case Protocol.TYPE_SCREEN_U_READY:
            case Protocol.TYPE_SCREEN_U_START:
            case Protocol.TYPE_SCREEN_U_STOP:
            case Protocol.TYPE_SNDOUT_U_READY:
            case Protocol.TYPE_SNDOUT_U_START:
            case Protocol.TYPE_SNDOUT_U_STOP:
            case Protocol.TYPE_CAMOUT_U_READY:
            case Protocol.TYPE_CAMOUT_U_START:
            case Protocol.TYPE_CAMOUT_U_STOP:
            case Protocol.TYPE_MICOUT_U_READY:
            case Protocol.TYPE_MICOUT_U_START:
            case Protocol.TYPE_MICOUT_U_STOP:
            case Protocol.TYPE_CAMFIX_U_READY:
            case Protocol.TYPE_CAMFIX_U_START:
            case Protocol.TYPE_CAMFIX_U_STOP:
            case Protocol.TYPE_MICFIX_U_READY:
            case Protocol.TYPE_MICFIX_U_START:
            case Protocol.TYPE_MICFIX_U_STOP:
            case Protocol.TYPE_INPUT_TOUCH_SINGLE:
            case Protocol.TYPE_INPUT_KEY_SINGLE:
            case Protocol.TYPE_INPUT_TEXT_SINGLE:
            case Protocol.TYPE_MCTL_REBOOT:
            case Protocol.TYPE_SYSTEM_REBOOT:
                _notify(_ptr_obj, data, size);
                break;
        }
    }

    @Override
    public void handle(int type, byte[] data, int size, byte[] params) {
        if (_ptr_obj < 0) return;
        //_handle(_ptr_obj, type, data, size, params);
    }

    public void startUserServer() {
        if (_ptr_obj < 0) return;
        _startUserServer(_ptr_obj);
    }

    public void stopUserServer() {
        if (_ptr_obj < 0) return;
        _stopUserServer(_ptr_obj);
    }

    public void startUserlSession(long uid, String sip) {
        if (_ptr_obj < 0) return;
        _startUserSession(_ptr_obj, uid, sip);
    }

    public void stopUserSession(String sip) {
        if (_ptr_obj < 0) return;
        _stopUserSession(_ptr_obj, sip);
    }

    public void startRtspServer() {
        if (_ptr_obj < 0) return;
        _startRtspServer(_ptr_obj);
    }

    public void stopRtspServer() {
        if (_ptr_obj < 0) return;
        _stopRtspServer(_ptr_obj);
    }

    public void startScreenServer(long tid) {
        if (_ptr_obj < 0) return;
        _startScreenServer(_ptr_obj, tid);
    }

    public void stopScreenServer(long tid) {
        if (_ptr_obj < 0) return;
        _stopScreenServer(_ptr_obj, tid);
    }

    public void startSndoutServer(long tid) {
        if (_ptr_obj < 0) return;
        _startSndoutServer(_ptr_obj, tid);
    }

    public void stopSndoutServer(long tid) {
        if (_ptr_obj < 0) return;
        _stopSndoutServer(_ptr_obj, tid);
    }

    private void javaNotifydata(byte[] data, int size) {
        Notify.get().notifydata(data, size);
    }

    private void javaHandleData(int type, byte[] data, int size, byte[] params) {
        Notify.get().handledata(type, data, size, params);
    }

    private native long _init();

    private native void _release(long p_obj);

    private native void _setTid(long tid);

    private native void _setUid(long uid);

    private native void _notify(long p_obj, byte[] data, int size);

    private native void _handle(long p_obj, int type, byte[] data, int size, byte[] params);

    private native void _startUserServer(long p_obj);

    private native void _stopUserServer(long p_obj);

    private native void _startUserSession(long p_obj, long uid, String sip);

    private native void _stopUserSession(long p_obj, String sip);

    private native void _startRtspServer(long p_obj);

    private native void _stopRtspServer(long p_obj);

    private native void _startScreenServer(long p_obj, long tid);

    private native void _stopScreenServer(long p_obj, long tid);

    private native void _startSndoutServer(long p_obj, long tid);

    private native void _stopSndoutServer(long p_obj, long tid);

}
