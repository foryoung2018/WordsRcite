//
// Created by huizai on 2017/11/24.
//
#ifndef FFMPEG_DEMO_GLVIEW_H
#define FFMPEG_DEMO_GLVIEW_H

#include "JniDefine.h"

void initGL();
void setVideoSize(int width,int height);
void displayYUV420pData(H264YUV_Frame * frame);
/**
 清除画面
 */
void clearFrame();
void Render();
void SetupYUVTextures();


#endif