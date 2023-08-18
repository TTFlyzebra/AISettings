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
    void javaOnComplete(int errCode);

private:
    JavaVM* javeVM ;
    JNIEnv *jniEnv ;
    jobject jObject;
    jmethodID onVideoDecode;
    jmethodID onAudioDecode;
    jmethodID onComplete;
};


#endif //FLYZEBRAPLAYER_CALLBACK_H
