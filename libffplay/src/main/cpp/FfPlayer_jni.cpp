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
Java_com_flyzebra_ffplay_FfPlayer__1play(JNIEnv *env, jobject thiz, jstring jurl) {
    FLOGD("JNI ffmpeg play");
    auto *ffmpeg = new FfPlayer(javaVM, env, thiz);
    const char *surl = env->GetStringUTFChars(jurl, 0);
    ffmpeg->play(surl);
    env->ReleaseStringUTFChars(jurl, surl);
    return reinterpret_cast<jlong>(ffmpeg);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_flyzebra_ffplay_FfPlayer__1stop(JNIEnv *env, jobject thiz, jlong ffmpegPointer) {
    FLOGD("JNI ffmpeg close");
    auto *ffmpeg = reinterpret_cast<FfPlayer *>(ffmpegPointer);
    ffmpeg->stop();
    delete ffmpeg;
}