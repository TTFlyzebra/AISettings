//
// Created by Administrator on 2023/6/30.
//

#include <stdint.h>
#include "Fzebra.h"
#include "buffer/BufferManager.h"
#include "rfc/Protocol.h"
#include "utils/FlyLog.h"
#include "server/UserServer.h"
#include "client/UserSession.h"
#include "rtsp/RtspServer.h"
#include "service/SndOutService.h"
#include "service/ScreenService.h"
#include "utils/ByteUtil.h"
#include "base/Global.h"

Fzebra::Fzebra(JavaVM *jvm, JNIEnv *env, jobject thiz)
        : mUserServer(nullptr), mRtspServer(nullptr) {
    cb = new FzebraCB(jvm, env, thiz);
    BufferManager::get()->init();
    T = new Terminal();
    U = new User();
    N = new Notify();
    N->registerListener(this);
}

Fzebra::~Fzebra() {
    stopUserServer();
    startRtspServer();
    for (auto item: mUserSessions) {
        char sn[16] = {0};
        delete item.second;
    }
    mUserSessions.clear();
    for (auto item: mScreens) {
        char sn[16] = {0};
        delete item.second;
    }
    mScreens.clear();
    for (auto item: mSndOuts) {
        char sn[16] = {0};
        delete item.second;
    }
    mSndOuts.clear();

    N->unregisterListener(this);
    delete cb;
    delete T;
	delete U;
    delete N;
    //Notify使用了BufferMangaer,所以要在Notify后面释放
    BufferManager::get()->release();
}

void Fzebra::notify(const char *data, int32_t size) {
    auto *notifyData = (NotifyData *) data;
    switch (notifyData->type) {
        case TYPE_TU_HEARTBEAT:
        case TYPE_T_CONNECTED:
        case TYPE_T_DISCONNECTED:
        case TYPE_U_CONNECTED:
        case TYPE_U_DISCONNECTED:
        case TYPE_T_INFO:
        case TYPE_SCREEN_T_START:
        case TYPE_SCREEN_T_STOP:
        case TYPE_SNDOUT_T_START:
        case TYPE_SNDOUT_T_STOP:
        case TYPE_CAMOUT_T_START:
        case TYPE_CAMOUT_T_STOP:
        case TYPE_MICOUT_T_START:
        case TYPE_MICOUT_T_STOP:
        case TYPE_CAMFIX_T_START:
        case TYPE_CAMFIX_T_STOP:
        case TYPE_MICFIX_T_START:
        case TYPE_MICFIX_T_STOP:
        case TYPE_CAMERA_OPEN:
        case TYPE_CAMERA_CLOSE:
            cb->javaNotifydata(data, size);
            break;
    }
}

void Fzebra::handle(NofifyType type, const char *data, int32_t size, const char *params) {
    switch (type) {
        case NOTI_SNDOUT_PCM:
            break;
        case NOTI_SCREEN_YUV:
            cb->javaHandledata(type, data, size, params, 24);
            break;
    }
}

void Fzebra::nativeNotifydata(const char *data, int32_t size) {
    N->notifydata(data, size);
}

void Fzebra::nativeHandledata(NofifyType type, const char *data, int32_t size, const char *parmas) {
    N->handledata(type, data, size, parmas);
}

void Fzebra::setTid(int64_t tid) {
    T->tid = tid;
}

void Fzebra::setUid(int64_t uid) {
    U->uid = uid;
}

void Fzebra::startUserServer() {
    mUserServer = new UserServer(N);
}

void Fzebra::stopUserServer() {
    if (mUserServer) {
        delete mUserServer;
        mUserServer = nullptr;
    }
}

void Fzebra::startUserSession(int64_t uid, const char *sip) {
    uint32_t id = inet_addr(sip);
    auto it = mUserSessions.find(id);
    if (it == mUserSessions.end()) {
        auto *userSession = new UserSession(N, uid, sip);
        FLOGI("UserlSession connect ip %s", sip);
        mUserSessions.emplace(id, userSession);
    }
}

void Fzebra::stopUserSession(const char *sip) {
    uint32_t id = inet_addr(sip);
    auto it = mUserSessions.find(id);
    if (it != mUserSessions.end()) {
        delete it->second;
    }
    mScreens.erase(id);
}

void Fzebra::startRtspServer() {
    mRtspServer = new RtspServer(N);
}

void Fzebra::stopRtspServer() {
    if (mRtspServer) {
        delete mRtspServer;
        mRtspServer = nullptr;
    }
}

void Fzebra::startScreenServer(int64_t tid) {
    auto it = mScreens.find(tid);
    if (it == mScreens.end()) {
        auto *screenService = new ScreenService(N, tid);
        mScreens.emplace(tid, screenService);
    }
}

void Fzebra::stopScreenServer(int64_t tid) {
    auto it = mScreens.find(tid);
    if (it != mScreens.end()) {
        delete it->second;
    }
    mScreens.erase(tid);
}

void Fzebra::startSndoutServer(int64_t tid) {
    auto it = mSndOuts.find(tid);
    if (it == mSndOuts.end()) {
        auto *sndOutService = new SndOutService(N, tid);
        mSndOuts.emplace(tid, sndOutService);
    }
}

void Fzebra::stopSndoutServer(int64_t tid) {
    auto it = mSndOuts.find(tid);
    if (it != mSndOuts.end()) {
        delete it->second;
    }
    mSndOuts.erase(tid);
}

