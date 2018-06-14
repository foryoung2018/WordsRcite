//
// Created by huizai on 2017/11/22.
//

#include "FFmpeg.h"
#include "GlView.h"
#include "Opensl_io.h"

using namespace std;
static          list<AVPacket>videos;
static          list<unsigned char*>audioDatas;
Decoder         *decoder = NULL;
H264YUV_Frame   yuvFrame;
AACFrame        *aacFrame;
pthread_mutex_t mutex_lock;
OPENSL_STREAM   *audioStream;
AVPacket        aPkt;
int             audioAndVideoTimeDiff;

void releaseSource();

extern "C" JNIEXPORT void JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_OpenFileWithUrl(
        JNIEnv *env,
        jobject obj,
        jstring input)
{
    releaseSource();
    //初始化一些数据 销毁plyer时要手动释放
    decoder = (Decoder*)malloc(sizeof(Decoder));
    //音频重采样指针初始化为空
    memset(&decoder->pkt,0, sizeof(AVPacket));
    memset(&aPkt,0, sizeof(AVPacket));
    decoder->pSwrCtx=NULL;
    decoder->pSwsCtx=NULL;
    aacFrame = (AACFrame*)malloc(sizeof(AACFrame));
    aacFrame->length = 0;
    memset(&yuvFrame,0, sizeof(H264YUV_Frame));
    pthread_mutex_init(&mutex_lock,NULL);
    audioAndVideoTimeDiff = 0;
    const char *inputc = env->GetStringUTFChars((jstring) input, 0);
    if (!decoder){
        FFLOGE("decoder is null");
    }
    decoder->url = inputc;
    int ret = OpenAndInitDecoder(decoder);
    if (ret !=0) {
        printError("InitDecoderFail:",ret);
    }
}

void releaseSource(){
    if (&mutex_lock){
        pthread_mutex_destroy(&mutex_lock);
    }
    if(aacFrame != NULL && aacFrame->dataBuffer){
        free(aacFrame->dataBuffer);
    }
    if (aacFrame){
        free(aacFrame);
    }
    if (yuvFrame.luma.dataBuffer){
        free(yuvFrame.luma.dataBuffer);
        free(yuvFrame.chromaR.dataBuffer);
        free(yuvFrame.chromaB.dataBuffer);
    }
    if (decoder && decoder->yuvFrame.luma.dataBuffer){
        free(decoder->yuvFrame.luma.dataBuffer);
        free(decoder->yuvFrame.chromaR.dataBuffer);
        free(decoder->yuvFrame.chromaB.dataBuffer);

    }
    if(&aPkt){
        av_packet_unref(&aPkt);
    }
    if (decoder && &decoder->pkt){
        free(&decoder->pkt);
    }
    if (decoder && &decoder->pYuv){
        av_frame_unref(decoder->pYuv);
    }
    if (decoder&& &decoder->pPcm){
        av_frame_unref(decoder->pPcm);
        free(decoder);
    }
    if (audioDatas.size()>0){
        for (int i = 0; i < audioDatas.size(); ++i) {
            unsigned char* tempBuf =  audioDatas.back();
            free(tempBuf);
        }
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_ReadPkt(
        JNIEnv *env,
        jobject obj)
{

    if (audioStream){
        if (audioStream->inputDataCount > 30){
            usleep(100);
            return -1;
        }
    }
    pthread_mutex_lock(&mutex_lock);
    int ret = Read(decoder);
    pthread_mutex_unlock(&mutex_lock);
    //读取pkt错误 返回
    if (ret == -9){
        //读取文件完毕
        return -9;
    }
    if (ret < 0){
        return -1;
    }
    //判断是否是音频，音频进行解码 视频返回-2不解码
    if(decoder->pkt.stream_index == decoder->videoStreamIndex){
        AVPacket pkt;
        pthread_mutex_lock(&mutex_lock);
        pkt = decoder->pkt;
        pthread_mutex_unlock(&mutex_lock);
        videos.push_back(pkt);
        return -2;
    } else if(decoder->pkt.stream_index == decoder->audioStreamIndex) {
        pthread_mutex_lock(&mutex_lock);
        aPkt = decoder->pkt;
        av_packet_unref(&decoder->pkt);
        pthread_mutex_unlock(&mutex_lock);
        return 0;
    } else{
        av_packet_unref(&decoder->pkt);
        return -1;
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_DecodeAudio(
        JNIEnv *env,
        jobject obj,
        jint ptrAddress
        )
{
    pthread_mutex_lock(&mutex_lock);
    Decode(decoder,aPkt);
    pthread_mutex_unlock(&mutex_lock);
    aacFrame->dataBuffer = (unsigned char*)malloc(10000);
    pthread_mutex_lock(&mutex_lock);
    int ret = ToPcm(decoder,aacFrame);
    pthread_mutex_unlock(&mutex_lock);
    if (aacFrame->length == 0){
        return -1;
    }
    if (audioStream == NULL){
        audioStream = android_OpenAudioDevice((uint32_t)decoder->sampleRate, 0, (uint32_t)decoder->channel,aacFrame->length);
        if (audioStream == NULL){
            FFLOGE("openAudioDevice fail");
            return -1;
        }
    }
    if (aacFrame->length > 10000) {
        free(aacFrame->dataBuffer);
        return -1;
    }
    //这里做一下音频数据缓存，后期释放
    pthread_mutex_lock(&mutex_lock);
    audioDatas.push_front(aacFrame->dataBuffer);
    ret = android_AudioOut(audioStream, (uint16_t*)aacFrame->dataBuffer, aacFrame->length);
    if (audioDatas.size()>70){
        unsigned char* tempBuf =  audioDatas.back();
        audioDatas.pop_back();
        free(tempBuf);
    }
    pthread_mutex_unlock(&mutex_lock);
    return 0;
}
extern "C" JNIEXPORT jint JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_DecodeVideo(
        JNIEnv *env,
        jobject obj,
        jint ptrAddress
        )
{
    if (videos.size()<=0){
        usleep(100);
        //没有视频帧
        return -1;
    }

//    FFLOGE("+++++++++++++++++++++vFps:%d",decoder->vFps);
//    FFLOGE("+++++++++++++++++++++aFps:%d",decoder->aFps);
    //上面这个条件是保证vfps在第一次解码视频前vfps异常导致死循环产生，解码一帧视频以后就肯定没问题了
    if (decoder->vFps > 600 && (decoder->vFps - decoder->aFps)<3000){
        if (decoder->vFps > decoder->aFps - 450 - audioAndVideoTimeDiff){
            usleep(100);
            return -1;
        }
    }
    pthread_mutex_lock(&mutex_lock);
    AVPacket vpkt = videos.front();
    if (vpkt.data == NULL){
        videos.pop_front();
        pthread_mutex_unlock(&mutex_lock);
        return -1;
    }
    int ret = Decode(decoder,vpkt);
    av_packet_unref(&vpkt);
    videos.pop_front();
    if (ret != 0){
        FFLOGE("Decode video fail");
        pthread_mutex_unlock(&mutex_lock);
        return -1;
    }
    //这里是判断dataBuffer里面的内存是否已经分配，本人实在找不到合适的判断方法
    if (decoder->yuvFrame.width > 0 && decoder->yuvFrame.width < 2000){
        free(decoder->yuvFrame.luma.dataBuffer);
        free(decoder->yuvFrame.chromaB.dataBuffer);
        free(decoder->yuvFrame.chromaR.dataBuffer);
    }
    ret = YuvToGLData(decoder);
    if(ret != 0){
        FFLOGE("to gl yuv data fail");
        pthread_mutex_unlock(&mutex_lock);
        return -1;
    }
    yuvFrame = decoder->yuvFrame;
    pthread_mutex_unlock(&mutex_lock);
    //FFLOGI("===============videosize:%d videoH:%d",videos.size(),frame.width);
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_SetPlayRate(
        JNIEnv *env,
        jobject obj,
        jint playRate
)
{
    return android_SetPlayRate(audioStream ,(int)playRate);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_SetPlayAudioOrVideoRate(
        JNIEnv *env,
        jobject obj,
        jint avPlayRate
)
{
    audioAndVideoTimeDiff -= avPlayRate;
}

/************************************************************************************
 * opengl
 */
extern "C" JNIEXPORT void JNICALL
Java_com_example_libraryvideo_surface_GLRenderer_InitOpenGL(
        JNIEnv *env,
        jobject obj)
{
    initGL();
}
extern "C" JNIEXPORT void JNICALL
Java_com_example_libraryvideo_surface_GLRenderer_OnViewportChanged(
        JNIEnv *env,
        jobject obj,
        jfloat width,
        jfloat height
)
{
    glViewport(0, 0, width, height);
}
extern "C" JNIEXPORT void JNICALL
Java_com_example_libraryvideo_surface_GLRenderer_RenderOneFrame(
        JNIEnv *env,
        jobject obj)
{
    pthread_mutex_lock(&mutex_lock);
    displayYUV420pData(&yuvFrame);
    pthread_mutex_unlock(&mutex_lock);
}



extern "C" JNIEXPORT void JNICALL
Java_com_example_libraryvideo_otherplay_AVPlayer_ThreadTest(
        JNIEnv *env,
        jobject obj,
        jstring input)
{
    jclass jcInfo = env->FindClass("com/example/libraryvideo/otherplay/AVPlayer");
    jclass clazz = env->GetObjectClass(obj);
    jfieldID  jfieldID1 = env->GetFieldID(jcInfo,"number","I");
    if (jfieldID1 == NULL){
        FFLOGI("======================%s","fieldid is null");
        return;
    }
    jint  jint1 = env->GetIntField(obj,jfieldID1);
    jint1 += jint1;
    env->SetIntField(obj,jfieldID1,jint1);
    jfieldID jfieldID2 = env->GetFieldID(clazz,"myStr","Ljava/lang/String;");
    if (jfieldID2 == NULL)
        return;
    jstring jstring1 = (jstring)env->GetObjectField(obj,jfieldID2);
    const char* mstr = env->GetStringUTFChars(jstring1,0);
    //  FFLOGI("======================%s",mstr);
    char test[] = "i'm from c";
    // sprintf(test,"%s",mstr);
    jstring jtestStr = env->NewStringUTF(test);
    env->SetObjectField(obj,jfieldID2,jtestStr);
    env->ReleaseStringChars(jstring1,(const jchar*)mstr);
//    env->ReleaseStringChars(jtestStr,(const jchar*)test);
}

