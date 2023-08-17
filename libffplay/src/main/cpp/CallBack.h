//
// Created by FlyZebra on 2020/9/15 0015.
//

#ifndef FLYZEBRAPLAYER_CALLBACK_H
#define FLYZEBRAPLAYER_CALLBACK_H
#include "jni.h"


class CallBack {
public:
    CallBack(JavaVM* jvm, JNIEnv *env, jobject thiz);
    ~CallBack();
    void javaOnVideoEncode(uint8_t *videoBytes, int width, int height, int size, long dts, long pts);
    void javaOnVideoDecode(uint8_t *videoBytes, int width, int height, int size);
    void javaOnAudioEncode(const uint8_t *audioBytes, int size);
    void javaOnAudioDecode(const uint8_t *audioBytes, int size);
    void javaOnVideoStart(int format, int width, int height,int fps, const uint8_t *sps, int len1, const uint8_t *pps, int len2);
    void javaOnAudioStart(int sampleRateInHz, int channelConfig, int audioFormat);
    void javaOnError(int error);
    void javaOnComplete();

private:
    JavaVM* javeVM ;
    JNIEnv *jniEnv ;
    jobject jObject;
    jmethodID onVideoEncode;
    jmethodID onVideoDecode;
    jmethodID onAudioEncode;
    jmethodID onAudioDecode;
    jmethodID onVideoStart;
    jmethodID onAudioStart;
    jmethodID onError;
    jmethodID onComplete;
};


#endif //FLYZEBRAPLAYER_CALLBACK_H
