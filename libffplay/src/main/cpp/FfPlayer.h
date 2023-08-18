//
// Created by flyzebra on 2018/11/9.
//

#ifndef FLYPLAYER_FLYFFMPEG_H
#define FLYPLAYER_FLYFFMPEG_H

#include <thread>
#include "FlyLog.h"
#include "CallBack.h"

class FfPlayer {

public:
    FfPlayer(JavaVM* jvm, JNIEnv *env, jobject thiz, const char* url);
    ~FfPlayer();
    void play();
    void playThread();
    void stop();
    static int interrupt_cb(void *ctx);

private:
    volatile bool is_stop = false;
    CallBack *callBack = nullptr;
    char play_url[255] = {0};
    std::thread* play_t = nullptr;
};

#endif //FLYPLAYER_FLYFFMPEG_H
