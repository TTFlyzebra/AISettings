package com.flyzebra.ffplay;

/**
 * Author: FlyZebra
 * Time: 18-5-13 下午7:09.
 * Discription: This is IRtspCallBack
 */
public interface IFfPlayer {

    void onVideoDecode(byte[] videoBytes, int size, int width, int height);

    void onAudioDecode(byte[] audioBytes, int size, int sampleRateInHz, int channelConfig, int audioFormat);

    void onError(int error);

    void onComplete();
}
