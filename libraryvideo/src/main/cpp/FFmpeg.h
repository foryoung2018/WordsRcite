//
// Created by huizai on 2017/11/22.
//

#ifndef FFMPEG_DEMO_FFMPEGDECODER_H
#define FFMPEG_DEMO_FFMPEGDECODER_H
#include "JniDefine.h"
#include "YUV_GL_DATA.h"

extern "C" {
//编码
#include "libavcodec/avcodec.h"
//封装格式处理
#include "libavformat/avformat.h"
//像素处理
#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
}

//定义音频重采样后的参数
#define SAMPLE_SIZE 16
#define SAMPLE_RATE 44100
#define CHANNEL     2


typedef struct  FFmpeg{
    int totleMs;
//音频播放时间
    int aFps;
//视频播放时间
    int vFps;
//视频流索引
    int videoStreamIndex;
    int audioStreamIndex;
    int sampleRate;
    int sampleSize;
    int channel;
//音频贞数据的长度
    int pcmDataLength;

    AVFormatContext   * pFormatCtx;
    AVCodecContext    * pVideoCodecCtx;
    AVCodecContext    * pAudioCodecCtx;
    AVFrame           * pYuv;
    AVFrame           * pPcm;
    AVCodec                 * pVideoCodec; //视频解码器
    AVCodec                 * pAudioCodec; //音频解码器
    struct SwsContext      * pSwsCtx;
    SwrContext              * pSwrCtx;
    const char             * url;
    AVPacket                  pkt;
    char                    * audioData;
    H264YUV_Frame            yuvFrame;
}Decoder;

int   OpenAndInitDecoder(Decoder * decoder);
int   Read(Decoder * decoder);
int   Decode(Decoder * decoder,AVPacket packet);
int   YuvToGLData(Decoder *decoder);
int   ToPcm(Decoder * decoder,AACFrame * frame);
void  printError(const char* flag,int ret);
double r2d(AVRational r);
#endif //FFMPEG_DEMO_FFMPEGDECODER_H
