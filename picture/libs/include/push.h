//
// Created by foryoung on 2018/7/23.
//
#include <jni.h>
#ifndef WORDSRCITE_PUSH_H
#define WORDSRCITE_PUSH_H

#endif //WORDSRCITE_PUSH_H


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_startPush(JNIEnv *env, jobject instance, jstring url_);


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_stopPush(JNIEnv *env, jobject instance);


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_release(JNIEnv *env, jobject instance);


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_setVideoOptions(JNIEnv *env, jobject instance, jint width,
jint height, jint bitrate, jint fps);


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_setAudioOptions(JNIEnv *env, jobject instance,
        jint sampleRateInHz, jint channel);


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_fireVideo(JNIEnv *env, jobject instance, jbyteArray data_);


JNIEXPORT void JNICALL
Java_com_hy_picture_push_PushNative_fireAudio(JNIEnv *env, jobject instance, jbyteArray data_,
jint len);