#include <jni.h>
#include <string.h>
#include <android/log.h>

extern "C"{
//编码
#include "libavcodec/avcodec.h"
//封装格式处理
#include "libavformat/avformat.h"
//像素处理
#include "libswscale/swscale.h"
}
#define FFLOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"ffmpeg",FORMAT,##__VA_ARGS__);
#define FFLOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"ffmpeg",FORMAT,##__VA_ARGS__);

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   hello-jni/app/src/main/java/com/example/hellojni/HelloJni.java
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_libraryvideo_ActivityOne_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif

    return (env)->NewStringUTF( "Hello from JNI !  Compiled with ABI " ABI ".");
}



extern "C"
JNIEXPORT void JNICALL Java_com_example_libraryvideo_ActivityOne_FFmpegTest(
        JNIEnv *env,
        jobject obj,
        jstring input,
        jstring output
) {
    //获取输入输出文件名
    const char *inputc = env->GetStringUTFChars((jstring) input, 0);
    const char *outputc = env->GetStringUTFChars((jstring) output, 0);
    //1.注册所有组件
    av_register_all();
    avformat_network_init();
    //2.打开输入视频文件
    //封装格式上下文，统领全局的结构体，保存了视频文件封装格式的相关信息
    AVFormatContext *pFormatCtx = avformat_alloc_context();
    int ret = avformat_open_input(&pFormatCtx, inputc, NULL, NULL);
    if (ret != 0) {
        char errorbuf[1024] = {0};
        av_make_error_string(errorbuf, 1024, ret);
        FFLOGE("%s,%d,%s", "无法打开输入视频文件", ret, errorbuf);
        FFLOGE("%s", inputc);
    } else {
        FFLOGI("%s,%d,%ld", "视频长度：", ret, pFormatCtx->duration);
    }
}
extern "C"
JNIEXPORT jint Java_com_example_libraryvideo_ActivityOne_JniCppAdd(JNIEnv*env, jobject obj,jint a,jint b){
    int ia = a;
    int ib = b;
    printf("==================%d",a+b);
    return a+b;
}
extern "C"
JNIEXPORT jint Java_com_example_libraryvideo_ActivityOne_JniCppSub(JNIEnv*env, jobject obj,jint a,jint b){
    return a-b;
}