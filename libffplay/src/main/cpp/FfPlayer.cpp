//
// Created by flyzebra on 2018/11/9.
//

#include <unistd.h>
#include <sys/system_properties.h>
#include <regex.h>
#include <dirent.h>

#include "stdatomic.h"
#include "FfPlayer.h"
#include "FfPlayer.h"
#include <cstdio>
#include <cstdlib>
#include <ctime>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include "libavutil/imgutils.h"
#include <libavutil/opt.h>
}

FfPlayer::FfPlayer(JavaVM *jvm, JNIEnv *env, jobject thiz) {
    FLOGI("%s()", __func__);
    this->callBack = new CallBack(jvm, env, thiz);
}

FfPlayer::~FfPlayer() {
    delete this->callBack;
    FLOGI("%s()", __func__);
}

int FfPlayer::interrupt_cb(void *ctx) {
    auto *p = (FfPlayer *) ctx;
    if (p->is_stop) {
        FLOGE("FlyFFmpeg interrupt_cb, will exit! \n");
        return 1;
    } else {
        return 0;
    }
}

void FfPlayer::play(const char *url) {
    FLOGI("%s()", __func__);
    sprintf(playUrl, "%s", url);
    stop();
    is_stop = false;
    play_t = new std::thread(&FfPlayer::playThread, this);
}


void FfPlayer::playThread() {
    FLOGI("%s()", __func__);
    int videoStream = -1;
    int audioStream = -1;
    int count = 0;

    AVFormatContext *pFormatCtx;
    AVCodecContext *pCodecCtx_video = nullptr;
    AVCodecContext *pCodecCtx_audio = nullptr;
    AVPacket *packet = nullptr;
    AVFrame *frame = nullptr;
    struct SwrContext *swr_ctx = nullptr;
    uint8_t *pcm_buf = nullptr;

    FLOGI("play url %s", playUrl);
    av_register_all();
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();
    pFormatCtx->interrupt_callback.callback = interrupt_cb;
    pFormatCtx->interrupt_callback.opaque = (FfPlayer *) this;

    AVDictionary *avdic = nullptr;
    av_dict_set(&avdic, "stimeout", "3000000", 0);//设置超时3秒
    av_dict_set(&avdic, "rtsp_transport", "tcp", 0);
    av_dict_set(&avdic, "probesize", "100*1024", 0);
    av_dict_set(&avdic, "max_analyze_duration", "5 * AV_TIME_BASE", 0);
    int ret = avformat_open_input(&pFormatCtx, playUrl, nullptr, &avdic);
    av_dict_free(&avdic);
    if (ret != 0) {
        FLOGE("Couldn't open file %s: (ret:%d)", playUrl, ret);
        callBack->javaOnError(-1);
        goto EXITPLAY;
    }

    if (avformat_find_stream_info(pFormatCtx, nullptr) < 0) {
        FLOGE("Could't find stream infomation.");
        callBack->javaOnError(-1);
        goto EXITPLAY;
    }

    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            AVCodecParameters *pCodecPar_video = pFormatCtx->streams[i]->codecpar;
            AVCodec *pCodec_video = avcodec_find_decoder(pCodecPar_video->codec_id);
            if (pCodec_video != nullptr) {
                pCodecCtx_video = avcodec_alloc_context3(pCodec_video);
                ret = avcodec_parameters_to_context(pCodecCtx_video, pCodecPar_video);
                if (ret >= 0) {
                    if (avcodec_open2(pCodecCtx_video, pCodec_video, nullptr) >= 0) {
                        FLOGD("find videoStream codec_id=%d", pFormatCtx->streams[i]->codecpar->codec_id);
                        videoStream = i;
                        break;
                    } else {
                        FLOGE("Could not open video decodec.");
                    }
                } else {
                    FLOGE("avcodec_parameters_to_context() failed %d", ret);
                }
            } else {
                FLOGE(" not found video decodec.");
            }
        }
    }

    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            AVCodecParameters *pCodecPar_audio = pFormatCtx->streams[i]->codecpar;
            AVCodec *pCodec_audio = avcodec_find_decoder(pCodecPar_audio->codec_id);
            if (pCodec_audio != nullptr) {
                pCodecCtx_audio = avcodec_alloc_context3(pCodec_audio);
                ret = avcodec_parameters_to_context(pCodecCtx_audio, pCodecPar_audio);
                if (ret >= 0) {
                    if (avcodec_open2(pCodecCtx_audio, pCodec_audio, nullptr) >= 0) {
                        FLOGD("find audioStream = %d, sampleRateInHz = %d, channelConfig=%d, audioFormat=%d",
                              i, pCodecCtx_audio->sample_rate, pCodecCtx_audio->channel_layout,
                              pCodecCtx_audio->sample_fmt);
                        int in_ch_layout = pCodecCtx_audio->channel_layout;
                        if (pCodecCtx_audio->sample_fmt == 1) {
                            if (pCodecCtx_audio->channels > 1) {
                                in_ch_layout = AV_CH_LAYOUT_STEREO;
                            } else {
                                in_ch_layout = AV_CH_LAYOUT_MONO;
                            }
                        }
                        int32_t channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
                        av_samples_alloc(&pcm_buf, nullptr, channels, 48000,
                                         (enum AVSampleFormat) AV_SAMPLE_FMT_S16, 0);
                        audioStream = i;
                    } else {
                        avcodec_close(pCodecCtx_audio);
                        FLOGE("init audio codec failed 3!");
                    }
                } else {
                    FLOGE("init audio codec failed 2!");
                }
            } else {
                FLOGE("init audio codec failed 1!");
            }
            break;
        }
    }

    if (videoStream == -1 && audioStream == -1) {
        FLOGE("not find vedio stream or audio stream.");
        callBack->javaOnError(-1);
        goto EXITPLAY;
    }

    frame = av_frame_alloc();
    packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    while (!is_stop && av_read_frame(pFormatCtx, packet) >= 0) {
        if (packet->stream_index == videoStream) {
            ret = avcodec_send_packet(pCodecCtx_video, packet);
            while (ret >= 0) {
                ret = avcodec_receive_frame(pCodecCtx_video, frame);
                if (ret >= 0) {
                    int width = frame->width;
                    int height = frame->height;
                    int32_t size = width * height * 3 / 2;
                    uint8_t *video_buf = (uint8_t *) malloc((size) * sizeof(uint8_t));
                    int32_t start = 0;
                    memcpy(video_buf, frame->data[0], width * height);
                    start = start + width * height;
                    memcpy(video_buf + start, frame->data[1], width * height / 4);
                    start = start + width * height / 4;
                    memcpy(video_buf + start, frame->data[2], width * height / 4);
                    callBack->javaOnVideoDecode(video_buf, size, width, height);
                    free(video_buf);
                    av_frame_unref(frame);
                }
            }
        } else if (packet->stream_index == audioStream) {
            ret = avcodec_send_packet(pCodecCtx_audio, packet);
            while (ret >= 0) {
                ret = avcodec_receive_frame(pCodecCtx_audio, frame);
                if (ret >= 0) {
                    if (!swr_ctx) {
                        swr_ctx = swr_alloc();
                        av_opt_set_int(swr_ctx, "in_channel_layout", frame->channel_layout, 0);
                        av_opt_set_int(swr_ctx, "out_channel_layout", AV_CH_LAYOUT_STEREO, 0);
                        av_opt_set_int(swr_ctx, "in_sample_rate", frame->sample_rate, 0);
                        av_opt_set_int(swr_ctx, "out_sample_rate", 48000, 0);
                        av_opt_set_sample_fmt(swr_ctx, "in_sample_fmt", (AVSampleFormat)frame->format, 0);
                        av_opt_set_sample_fmt(swr_ctx, "out_sample_fmt", (AVSampleFormat)AV_SAMPLE_FMT_S16, 0);
                        swr_init(swr_ctx);
                    }
                    int64_t delay = swr_get_delay(swr_ctx, frame->sample_rate);
                    int64_t out_count = av_rescale_rnd(
                            frame->nb_samples + delay,
                            48000,
                            frame->sample_rate,
                            AV_ROUND_UP);
                    int32_t retLen = swr_convert(
                            swr_ctx,
                            &pcm_buf,
                            out_count,
                            (const uint8_t**)frame->data,
                            frame->nb_samples);
                     if (retLen > 0) {
                        callBack->javaOnAudioDecode(pcm_buf, retLen * 4, 48000,
                                                    AV_CH_LAYOUT_STEREO,
                                                    AV_SAMPLE_FMT_S16);
                    }
                    av_frame_unref(frame);
                }
            }
        }
        av_packet_unref(packet);
    }
    EXITPLAY:
    if (swr_ctx) {
        FLOGD("swr_free swr_cxt.");
        swr_free(&swr_ctx);
    }
    if (pcm_buf) {
        FLOGD("av_freep audio_buf.");
        av_freep(&pcm_buf);
    }
    if (packet) {
        FLOGD("av_free packet.");
        av_packet_free(&packet);
    }
    if (frame) {
        FLOGD("av_frame_free frame.");
        av_frame_free(&frame);
    }
    if (pCodecCtx_video) {
        FLOGD("avcodec_close pCodecCtx_video.");
        avcodec_close(pCodecCtx_video);
    }
    if (pCodecCtx_audio) {
        FLOGD("avcodec_close pCodecCtx_audio.");
        avcodec_close(pCodecCtx_audio);
    }
    if (pFormatCtx) {
        FLOGD("avformat_close_input pFormatCtx.");
        avformat_close_input(&pFormatCtx);
    }
    if (!is_stop) {
        callBack->javaOnComplete();
    }
}

void FfPlayer::stop() {
    is_stop = true;
    if (play_t) {
        play_t->join();
        play_t = nullptr;
    }
}


