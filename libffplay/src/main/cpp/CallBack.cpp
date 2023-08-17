//
// Created by FlyZebra on 2020/9/15 0015.
//
#include <sys/types.h>
#include "FlyLog.h"
#include "CallBack.h"


CallBack::CallBack(JavaVM* jvm, JNIEnv *env, jobject thiz) {
    FLOGI("%s()", __func__);
    javeVM = jvm;
    jniEnv = env;
    jObject = jniEnv->NewGlobalRef(thiz);
    jclass  cls = jniEnv->GetObjectClass(jObject);
    if(!cls) {
        FLOGE("find jclass faild");
        return;
    }
    onVideoEncode = jniEnv->GetMethodID(cls, "onVideoEncode", "([BIIIJJ)V");
    onVideoDecode = jniEnv->GetMethodID(cls, "onVideoDecode", "([BIII)V");
    onAudioEncode = jniEnv->GetMethodID(cls, "onAudioEncode", "([BI)V");
    onAudioDecode = jniEnv->GetMethodID(cls, "onAudioDecode", "([BI)V");
    onVideoStart = jniEnv->GetMethodID(cls, "onVideoStart", "(III[B[B)V");
    onAudioStart = jniEnv->GetMethodID(cls, "onAudioStart", "(III)V");
    onError = jniEnv->GetMethodID(cls, "onError", "(I)V");
    onComplete = jniEnv->GetMethodID(cls, "onComplete", "()V");
    jniEnv->DeleteLocalRef(cls);
}

CallBack::~CallBack() {
    FLOGI("%s()", __func__);
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onVideoEncode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->DeleteGlobalRef(jObject);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }

}

void CallBack::javaOnVideoEncode(uint8_t *videoBytes, int width, int height, int size, long dts, long pts) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onVideoEncode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes = jniEnv->NewByteArray(static_cast<jsize>(size));
    jniEnv->SetByteArrayRegion(jbytes, 0, size, reinterpret_cast<const jbyte *>(videoBytes));
    jniEnv->CallVoidMethod(jObject, onVideoEncode, jbytes, width, height, size, dts, pts);
    jniEnv->DeleteLocalRef(jbytes);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnVideoDecode(uint8_t *videoBytes,int width, int height, int size) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onVideoDecode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes = jniEnv->NewByteArray(static_cast<jsize>(size));
    jniEnv->SetByteArrayRegion(jbytes, 0, size, reinterpret_cast<const jbyte *>(videoBytes));
    jniEnv->CallVoidMethod(jObject, onVideoDecode, jbytes, width, height,  size);
    jniEnv->DeleteLocalRef(jbytes);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnAudioEncode(const uint8_t *audioBytes, int length) {\
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onAudioEncode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes = jniEnv->NewByteArray(static_cast<jsize>(length));
    jniEnv->SetByteArrayRegion(jbytes, 0, length, reinterpret_cast<const jbyte *>(audioBytes));
    jniEnv->CallVoidMethod(jObject, onAudioEncode, jbytes, length);
    jniEnv->DeleteLocalRef(jbytes);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnAudioDecode(const uint8_t *audioBytes, int length) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onAudioDecode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes = jniEnv->NewByteArray(static_cast<jsize>(length));
    jniEnv->SetByteArrayRegion(jbytes, 0, length, reinterpret_cast<const jbyte *>(audioBytes));
    jniEnv->CallVoidMethod(jObject, onAudioDecode, jbytes, length);
    jniEnv->DeleteLocalRef(jbytes);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnVideoStart(int format, int width, int height, int fps, const uint8_t *sps, int len1, const uint8_t *pps, int len2) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onVideoStart: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes1 = jniEnv->NewByteArray(static_cast<jsize>(len1));
    jniEnv->SetByteArrayRegion(jbytes1, 0, len1, reinterpret_cast<const jbyte *>(sps));
    jbyteArray jbytes2 = jniEnv->NewByteArray(static_cast<jsize>(len2));
    jniEnv->SetByteArrayRegion(jbytes2, 0, len2, reinterpret_cast<const jbyte *>(pps));
    jniEnv->CallVoidMethod(jObject, onVideoStart, width,height,fps,jbytes1, jbytes2);
    jniEnv->DeleteLocalRef(jbytes1);
    jniEnv->DeleteLocalRef(jbytes2);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnAudioStart(int sampleRateInHz, int channelConfig, int audioFormat) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onAudioStart: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->CallVoidMethod(jObject, onAudioStart, sampleRateInHz,channelConfig,audioFormat);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnError(int error){
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onStop: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->CallVoidMethod(jObject, onError, error);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnComplete() {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if(status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if(status < 0) {
            FLOGE("onStop: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->CallVoidMethod(jObject, onComplete);
    if(isAttacked){
        (javeVM)->DetachCurrentThread();
    }
}