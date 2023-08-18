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
    void javaOnVideoDecode(uint8_t *videoBytes, int size, int width, int height);
    void javaOnAudioDecode(const uint8_t *audioBytes, int size, int sampleRateInHz, int channelConfig, int audioFormat);
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
