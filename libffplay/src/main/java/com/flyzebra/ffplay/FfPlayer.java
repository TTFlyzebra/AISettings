package com.flyzebra.ffplay;

import android.os.Handler;
import android.os.Looper;

import com.flyzebra.utils.FlyLog;

/**
 * Author: FlyZebra
 * Time: 18-5-13 下午6:55.
 * Discription: This is FlyClient
 */
public class FfPlayer {
    private IFfPlayer iFfPlayer;
    private long objPtr = -1;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    static {
        System.loadLibrary("ffplayer");
    }

    public FfPlayer() {
        FlyLog.d("new FfPlayer.");
        objPtr = -1;
    }

    public void open(IFfPlayer iFfPlayer, String url) {
        this.iFfPlayer = iFfPlayer;
        if (objPtr != -1) {
            _stop(objPtr);
        }
        objPtr = _open(url);
        play();
    }

    private void play() {
        if (objPtr != -1) {
            _play(objPtr);
        }
    }

    public void stop() {
        if (objPtr != -1) {
            _stop(objPtr);
            close();
        }
    }

    private void close() {
        if (objPtr != -1) {
            _close(objPtr);
        }
        objPtr = -1;
        iFfPlayer = null;
    }

    public void onVideoDecode(byte[] videoBytes, int size, int width, int height) {
        if (iFfPlayer != null) {
            iFfPlayer.onVideoDecode(videoBytes, size, width, height);
        }
    }

    public void onAudioDecode(byte[] audioBytes, int size, int sampleRateInHz, int channelConfig, int audioFormat) {
        if (iFfPlayer != null) {
            iFfPlayer.onAudioDecode(audioBytes, size, sampleRateInHz, channelConfig, audioFormat);
        }
    }

    public void onComplete(int errCode) {
        mHandler.post(() -> {
            if (iFfPlayer != null) iFfPlayer.onComplete();
        });
    }

    private native long _open(String url);

    private native void _play(long objPtr);

    private native void _stop(long objPtr);

    private native void _close(long objPtr);


}
