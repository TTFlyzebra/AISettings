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

FfPlayer::FfPlayer(JavaVM *jvm, JNIEnv *env, jobject thiz, const char *url) {
    //FLOGI("%s()", __func__);
    sprintf(play_url, "%s", url);
    this->callBack = new CallBack(jvm, env, thiz);
}

FfPlayer::~FfPlayer() {
    delete this->callBack;
    //FLOGI("%s()", __func__);
}

int FfPlayer::interrupt_cb(void *ctx) {
    auto *p = (FfPlayer *) ctx;
    if (p->is_stop) {
        FLOGE("FfPlayer interrupt_cb, will exit! \n");
        return 1;
    } else {
        return 0;
    }
}

void FfPlayer::play() {
    is_stop = false;
    play_t = new std::thread(&FfPlayer::playThread, this);
}

void FfPlayer::playThread() {
    FLOGI("will play url %s", play_url);
    int32_t errCode = 0;
    int32_t videoStream = -1;
    int32_t audioStream = -1;

    AVFormatContext *fmt_ctx = nullptr;
    AVCodecContext *v_codec = nullptr;
    AVCodecContext *a_codec = nullptr;

    AVPacket *packet = nullptr;
    AVFrame *frame = nullptr;

    uint8_t *yuv_buf = nullptr;
    uint8_t *pcm_buf = nullptr;
    struct SwrContext *swr_ctx = nullptr;

    av_register_all();
    avformat_network_init();

    fmt_ctx = avformat_alloc_context();
    fmt_ctx->interrupt_callback.callback = interrupt_cb;
    fmt_ctx->interrupt_callback.opaque = (FfPlayer *) this;

    AVDictionary *avdic = nullptr;
    //av_dict_set(&avdic, "stimeout", "5000000", 0);//设置超时5秒
    //av_dict_set(&avdic, "rtsp_transport", "tcp", 0);//使用TCP播放RTSP
    //av_dict_set(&avdic, "probesize", "100*1024", 0);
    //av_dict_set(&avdic, "max_analyze_duration", "5 * AV_TIME_BASE", 0);
    int ret = avformat_open_input(&fmt_ctx, play_url, nullptr, &avdic);
    av_dict_free(&avdic);
    if (ret != 0) {
        FLOGE("Couldn't open file %s: (ret:%d)", play_url, ret);
        errCode = -1;
        goto EXIT;
    }

    if (avformat_find_stream_info(fmt_ctx, nullptr) < 0) {
        FLOGE("Could't find stream infomation.");
        errCode = -2;
        goto EXIT;
    }

    for (int i = 0; i < fmt_ctx->nb_streams; i++) {
        if (fmt_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            AVCodecParameters *pCodecPar_video = fmt_ctx->streams[i]->codecpar;
            AVCodec *pCodec_video = avcodec_find_decoder(pCodecPar_video->codec_id);
            if (pCodec_video != nullptr) {
                v_codec = avcodec_alloc_context3(pCodec_video);
                ret = avcodec_parameters_to_context(v_codec, pCodecPar_video);
                if (ret >= 0) {
                    if (avcodec_open2(v_codec, pCodec_video, nullptr) >= 0) {
                        FLOGD("find videoStream codec_id=%d",
                              fmt_ctx->streams[i]->codecpar->codec_id);
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

    for (int i = 0; i < fmt_ctx->nb_streams; i++) {
        if (fmt_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            AVCodecParameters *pCodecPar_audio = fmt_ctx->streams[i]->codecpar;
            AVCodec *pCodec_audio = avcodec_find_decoder(pCodecPar_audio->codec_id);
            if (pCodec_audio != nullptr) {
                a_codec = avcodec_alloc_context3(pCodec_audio);
                ret = avcodec_parameters_to_context(a_codec, pCodecPar_audio);
                if (ret >= 0) {
                    if (avcodec_open2(a_codec, pCodec_audio, nullptr) >= 0) {
                        FLOGD("find audioStream=%d, sampleRateInHz=%d, channelConfig=%d, audioFormat=%d",
                              i, a_codec->sample_rate, a_codec->channel_layout,
                              a_codec->sample_fmt);
                        int32_t channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
                        av_samples_alloc(&pcm_buf, nullptr, channels, 48000,
                                         (enum AVSampleFormat) AV_SAMPLE_FMT_S16, 0);
                        audioStream = i;
                    } else {
                        avcodec_close(a_codec);
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
        errCode = -3;
        goto EXIT;
    }

    frame = av_frame_alloc();
    packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    while (!is_stop && av_read_frame(fmt_ctx, packet) >= 0) {
        if (packet->stream_index == videoStream) {
            ret = avcodec_send_packet(v_codec, packet);
            while (ret >= 0) {
                ret = avcodec_receive_frame(v_codec, frame);
                if (ret >= 0) {
                    int32_t width = frame->linesize[0];
                    int32_t height = frame->height;
                    int32_t size = width * height * 3 / 2;
                    if (!yuv_buf) yuv_buf = (uint8_t *) malloc((size) * sizeof(uint8_t));
                    memcpy(yuv_buf, frame->data[0], width * height);
                    memcpy(yuv_buf + width * height, frame->data[1], width * height / 4);
                    memcpy(yuv_buf + width * height * 5 / 4, frame->data[2], width * height / 4);
                    callBack->javaOnVideoDecode(yuv_buf, size, width, height);
                }
                av_frame_unref(frame);
            }
        } else if (packet->stream_index == audioStream) {
            ret = avcodec_send_packet(a_codec, packet);
            while (ret >= 0) {
                ret = avcodec_receive_frame(a_codec, frame);
                if (ret >= 0) {
                    if (!swr_ctx) {
                        swr_ctx = swr_alloc();
                        av_opt_set_int(swr_ctx, "in_channel_layout", frame->channel_layout, 0);
                        av_opt_set_int(swr_ctx, "out_channel_layout", AV_CH_LAYOUT_STEREO, 0);
                        av_opt_set_int(swr_ctx, "in_sample_rate", frame->sample_rate, 0);
                        av_opt_set_int(swr_ctx, "out_sample_rate", 48000, 0);
                        av_opt_set_sample_fmt(swr_ctx, "in_sample_fmt",
                                              (AVSampleFormat) frame->format, 0);
                        av_opt_set_sample_fmt(swr_ctx, "out_sample_fmt",
                                              (AVSampleFormat) AV_SAMPLE_FMT_S16, 0);
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
                            (const uint8_t **) frame->data,
                            frame->nb_samples);
                    if (retLen > 0) {
                        callBack->javaOnAudioDecode(pcm_buf, retLen * 4, 48000,
                                                    AV_CH_LAYOUT_STEREO,
                                                    AV_SAMPLE_FMT_S16);
                    }
                }
                av_frame_unref(frame);
            }
        }
        av_packet_unref(packet);
    }

    EXIT:
    if (swr_ctx) {
        FLOGD("swr_free swr_cxt.");
        swr_free(&swr_ctx);
    }
    if (yuv_buf) {
        FLOGD("free yuv_buf.");
        free(yuv_buf);
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
    if (v_codec) {
        FLOGD("avcodec_close pCodecCtx_video.");
        avcodec_close(v_codec);
    }
    if (a_codec) {
        FLOGD("avcodec_close pCodecCtx_audio.");
        avcodec_close(a_codec);
    }
    if (fmt_ctx) {
        FLOGD("avformat_close_input pFormatCtx.");
        avformat_close_input(&fmt_ctx);
    }
    callBack->javaOnComplete(errCode);
}

void FfPlayer::stop() {
    is_stop = true;
    if (play_t) {
        play_t->join();
        play_t = nullptr;
    }
}


