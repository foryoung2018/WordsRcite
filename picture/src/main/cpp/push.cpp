//
// Created by foryoung on 2018/7/23.
//

#include <jni.h>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "push.h"

extern "C" {
//#include <x264.h>
}

#define  LOG_TAG    "huyang picture module"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_startPush(JNIEnv *env, jobject instance, jstring url_) {
const char *url = env->GetStringUTFChars(url_, 0);
    LOGD("start");
// TODO

env->ReleaseStringUTFChars(url_, url);
}extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_stopPush(JNIEnv *env, jobject instance) {

// TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_release(JNIEnv *env, jobject instance) {

// TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_setVideoOptions(JNIEnv *env, jobject instance, jint width,
                                                    jint height, jint bitrate, jint fps) {

//    x264_param_t param;
//    //x264_param_default_preset 设置
//    x264_param_default_preset(&param,"ultrafast","zerolatency");
//    //编码输入的像素格式YUV420P
//    param.i_csp = X264_CSP_I420;
//    param.i_width  = width;
//    param.i_height = height;
//    //参数i_rc_method表示码率控制，CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
//    //恒定码率，会尽量控制在固定码率
//    param.rc.i_rc_method = X264_RC_CRF;
//    param.rc.i_bitrate = bitrate / 1000; //* 码率(比特率,单位Kbps)
//    param.rc.i_vbv_max_bitrate = bitrate / 1000 * 1.2; //瞬时最大码率
//
//    //码率控制不通过timebase和timestamp，而是fps
//    param.b_vfr_input = 0;
//    param.i_fps_num = fps; //* 帧率分子
//    param.i_fps_den = 1; //* 帧率分母
//    param.i_timebase_den = param.i_fps_num;
//    param.i_timebase_num = param.i_fps_den;
//    param.i_threads = 1;//并行编码线程数量，0默认为多线程
//
//    //是否把SPS和PPS放入每一个关键帧
//    //SPS Sequence Parameter Set 序列参数集，PPS Picture Parameter Set 图像参数集
//    //为了提高图像的纠错能力
//    param.b_repeat_headers = 1;
//    //设置Level级别
//    param.i_level_idc = 51;
//    //设置Profile档次
//    //baseline级别，没有B帧
//    x264_param_apply_profile(&param,"baseline");
//
//    //x264_picture_t（输入图像）初始化
//    x264_picture_t pic_in;
//    x264_picture_alloc(&pic_in, param.i_csp, param.i_width, param.i_height);
//
//    //打开编码器
//    x264_t *x264_encoder = x264_encoder_open(&param);
//    if(x264_encoder){
//        LOGI("打开编码器成功...");
//    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_setAudioOptions(JNIEnv *env, jobject instance,
                                                    jint sampleRateInHz, jint channel) {

    // TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_fireVideo(JNIEnv *env, jobject instance, jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO

    env->ReleaseByteArrayElements(data_, data, 0);
}extern "C"
JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_fireAudio(JNIEnv *env, jobject instance, jbyteArray data_,
                                              jint len) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO

    env->ReleaseByteArrayElements(data_, data, 0);
}