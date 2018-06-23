//
// Created by huizai on 2017/11/22.
//

#pragma once
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <list>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <GLES/egl.h>
#include <EGL/eglext.h>

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <math.h>
#include <malloc.h>
#include <android/log.h>
#include <string>
#include "YUV_GL_DATA.h"

#define FFLOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"ffmpeg",FORMAT,##__VA_ARGS__);
#define FFLOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"ffmpeg",FORMAT,##__VA_ARGS__);


