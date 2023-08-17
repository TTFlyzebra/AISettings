//
// Created by flyzebra on 2018/11/9.
//

#ifndef FLYPLAYER_FLYLOG_H
#define FLYPLAYER_FLYLOG_H
#include <android/log.h>
#define TAG "ZEBRA-WCAM-JNI" // 这个是自定义的LOG的标识
#define FLOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define FLOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define FLOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define FLOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型

#endif //FLYPLAYER_FLYLOG_H
