package com.flyzebra.ffplay;

import com.flyzebra.utils.FlyLog;

/**
 * Author: FlyZebra
 * Time: 18-5-13 下午6:55.
 * Discription: This is FlyClient
 */
public class FfPlayer {
    private IFfPlayer iFfPlayer;
    private long ffmpegPointer = -1;

    static {
        System.loadLibrary("ffplayer");
    }

    public FfPlayer() {
        FlyLog.d("new FfPlayer.");
        ffmpegPointer = -1;
    }

    public void play(IFfPlayer iFfPlayer, String url) {
        this.iFfPlayer = iFfPlayer;
        if (ffmpegPointer != -1) {
            _stop(ffmpegPointer);
        }
        ffmpegPointer = _play(url);
    }

    public void stop() {
        if (ffmpegPointer != -1) {
            _stop(ffmpegPointer);
        }
        ffmpegPointer = -1;
    }

    public void onVideoDecode(byte[] videoBytes, int size, int width, int height) {
        if (iFfPlayer != null) iFfPlayer.onVideoDecode(videoBytes, size, width, height);
    }

    public void onAudioDecode(byte[] audioBytes, int size, int sampleRateInHz, int channelConfig, int audioFormat) {
        if (iFfPlayer != null)
            iFfPlayer.onAudioDecode(audioBytes, size, sampleRateInHz, channelConfig, audioFormat);
    }

    public void onError(int error) {
        if (iFfPlayer != null) iFfPlayer.onError(error);
    }


    public void onComplete() {
        if (iFfPlayer != null) iFfPlayer.onComplete();
    }


    private native long _play(String url);

    private native void _stop(long objPointer);


}
