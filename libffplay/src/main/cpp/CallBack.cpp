//
// Created by FlyZebra on 2020/9/15 0015.
//
#include <sys/types.h>
#include "FlyLog.h"
#include "CallBack.h"


CallBack::CallBack(JavaVM *jvm, JNIEnv *env, jobject thiz) {
    FLOGI("%s()", __func__);
    javeVM = jvm;
    jniEnv = env;
    jObject = jniEnv->NewGlobalRef(thiz);
    jclass cls = jniEnv->GetObjectClass(jObject);
    if (!cls) {
        FLOGE("find jclass faild");
        return;
    }
    onVideoDecode = jniEnv->GetMethodID(cls, "onVideoDecode", "([BIII)V");
    onAudioDecode = jniEnv->GetMethodID(cls, "onAudioDecode", "([BIIII)V");
    onComplete = jniEnv->GetMethodID(cls, "onComplete", "(I)V");
    jniEnv->DeleteLocalRef(cls);
}

CallBack::~CallBack() {
    FLOGI("%s()", __func__);
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if (status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if (status < 0) {
            FLOGE("onVideoEncode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->DeleteGlobalRef(jObject);
    if (isAttacked) {
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnVideoDecode(uint8_t *videoBytes, int size, int width, int height) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if (status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if (status < 0) {
            FLOGE("onVideoDecode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes = jniEnv->NewByteArray(static_cast<jsize>(size));
    jniEnv->SetByteArrayRegion(jbytes, 0, size, reinterpret_cast<const jbyte *>(videoBytes));
    jniEnv->CallVoidMethod(jObject, onVideoDecode, jbytes, size, width, height);
    jniEnv->DeleteLocalRef(jbytes);
    if (isAttacked) {
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnAudioDecode(const uint8_t *audioBytes, int size, int sampleRateInHz,
                                 int channelConfig, int audioFormat) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if (status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if (status < 0) {
            FLOGE("onAudioDecode: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jbyteArray jbytes = jniEnv->NewByteArray(static_cast<jsize>(size));
    jniEnv->SetByteArrayRegion(jbytes, 0, size, reinterpret_cast<const jbyte *>(audioBytes));
    jniEnv->CallVoidMethod(jObject, onAudioDecode, jbytes, size, sampleRateInHz, channelConfig,
                           audioFormat);
    jniEnv->DeleteLocalRef(jbytes);
    if (isAttacked) {
        (javeVM)->DetachCurrentThread();
    }
}

void CallBack::javaOnComplete(int errCode) {
    int status = javeVM->GetEnv((void **) &jniEnv, JNI_VERSION_1_4);
    bool isAttacked = false;
    if (status < 0) {
        status = javeVM->AttachCurrentThread(&jniEnv, nullptr);
        if (status < 0) {
            FLOGE("onStop: failed to attach current thread");
            return;
        }
        isAttacked = true;
    }
    jniEnv->CallVoidMethod(jObject, onComplete, errCode);
    if (isAttacked) {
        (javeVM)->DetachCurrentThread();
    }
}