//
// Created by huizai on 2017/12/4.
//

#ifndef FFMPEG_DEMO_OPENSL_IO_H
#define FFMPEG_DEMO_OPENSL_IO_H

#include "JniDefine.h"

typedef struct opensl_stream {

    // engine interfaces
    SLObjectItf engineObject;
    SLEngineItf engineEngine;

    // output mix interfaces
    SLObjectItf outputMixObject;

    // buffer queue player interfaces
    SLObjectItf bqPlayerObject;
    SLPlayItf bqPlayerPlay;
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;

    SLObjectItf pitchObject;
    SLPitchItf bqPitchEngne;
    SLPlaybackRateItf bqPlayerRate;
    SLPitchItf bqPlayerVolume;


    // buffer indexes
    int      inputDataCount;
    double time;
    uint32_t outchannels; //输出的声道数量
    uint32_t sampleRate; //采样率

} OPENSL_STREAM;

int android_SetPlayRate(OPENSL_STREAM *p,int playRate);

/*
  Open the audio device with a given sampling rate (sr), input and output channels and IO buffer size
  in frames. Returns a handle to the OpenSL stream
*/
OPENSL_STREAM* android_OpenAudioDevice(uint32_t sr, uint32_t inchannels, uint32_t outchannels, uint32_t bufferframes);

/*
  Close the audio device
*/
void android_CloseAudioDevice(OPENSL_STREAM *p);

/*
  Write a buffer to the OpenSL stream *p, of size samples. Returns the number of samples written.
*/
uint32_t android_AudioOut(OPENSL_STREAM *p, uint16_t *buffer,uint32_t size);

class Opensl_io {

};
#endif //FFMPEG_DEMO_OPENSL_IO_H
