//
// Created by FlyZebra on 2018/11/8.
//

#include <jni.h>
#include "FfPlayer.h"
#include "CallBack.h"

JavaVM* javaVM = nullptr;

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env = nullptr;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        FLOGE("JNI OnLoad failed\n");
        return result;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_flyzebra_ffplay_FfPlayer__1open(JNIEnv *env, jobject thiz, jstring jurl) {
    const char *surl = env->GetStringUTFChars(jurl, 0);
    auto *ffplayer = new FfPlayer(javaVM, env, thiz, surl);
    env->ReleaseStringUTFChars(jurl, surl);
    return reinterpret_cast<jlong>(ffplayer);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_ffplay_FfPlayer__1play(JNIEnv *env, jobject thiz, jlong objPtr) {
    auto *ffplayer = reinterpret_cast<FfPlayer *>(objPtr);
    ffplayer->play();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_ffplay_FfPlayer__1stop(JNIEnv *env, jobject thiz, jlong objPtr) {
    auto *ffplayer = reinterpret_cast<FfPlayer *>(objPtr);
    ffplayer->stop();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_ffplay_FfPlayer__1close(JNIEnv *env, jobject thiz, jlong objPtr) {
    auto *ffplayer = reinterpret_cast<FfPlayer *>(objPtr);
    delete ffplayer;
}