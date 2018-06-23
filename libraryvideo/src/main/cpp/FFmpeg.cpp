//
// Created by huizai on 2017/11/22.
//

#include "FFmpeg.h"


int OpenAndInitDecoder(Decoder * decoder){

    //1.注册所有组件
    av_register_all();
    avformat_network_init();
    //2.打开输入视频文件
    //封装格式上下文，统领全局的结构体，保存了视频文件封装格式的相关信息
    decoder->pFormatCtx = avformat_alloc_context();
    int ret = avformat_open_input(&decoder->pFormatCtx, decoder->url, NULL, NULL);
    if (ret != 0) {
        return  ret;
    } else{
        FFLOGI("视频长度：%ld",decoder->pFormatCtx->duration);
    }
    decoder->totleMs = (int)(decoder->pFormatCtx->duration/AV_TIME_BASE)*1000;
    avformat_find_stream_info(decoder->pFormatCtx, NULL);
    //分别找到音频视频解码器并打开解码器
    for (int i = 0; i < decoder->pFormatCtx->nb_streams; i++) {
        AVStream *stream = decoder->pFormatCtx->streams[i];
        AVCodec * codec = avcodec_find_decoder(stream->codecpar->codec_id);
        AVCodecContext * codecCtx = avcodec_alloc_context3(codec);
        avcodec_parameters_to_context(codecCtx, stream->codecpar);

        if (codecCtx->codec_type == AVMEDIA_TYPE_VIDEO) {
            printf("video\n");
            decoder->videoStreamIndex = i;
            decoder->pVideoCodec  = codec;
            decoder->pVideoCodecCtx = codecCtx;
            int err = avcodec_open2(decoder->pVideoCodecCtx, decoder->pVideoCodec, NULL);
            if (err != 0) {
                char buf[1024] = {0};
                av_strerror(err, buf, sizeof(buf));
                printf("open videoCodec error:%s", buf);
                return false;
            }
        }
        if (codecCtx->codec_type == AVMEDIA_TYPE_AUDIO) {
            printf("audio\n");
            decoder->audioStreamIndex = i;
            decoder->pAudioCodec  = codec;
            decoder->pAudioCodecCtx = codecCtx;
            int err = avcodec_open2(decoder->pAudioCodecCtx, decoder->pAudioCodec, NULL);
            if (err != 0) {
                char buf[1024] = {0};
                av_strerror(err, buf, sizeof(buf));
                printf("open audionCodec error:%s", buf);
                return false;
            }
            if (codecCtx->sample_rate != SAMPLE_RATE) {
                decoder->sampleRate = codecCtx->sample_rate;
            }
        }
    }
    decoder ->pYuv = av_frame_alloc();
    decoder ->pPcm = av_frame_alloc();
    decoder ->channel = CHANNEL;
    decoder ->sampleSize = SAMPLE_SIZE;
    decoder ->sampleRate = decoder->pAudioCodecCtx->sample_rate;
    // printf("open acodec success! sampleRate:%d  channel:%d  sampleSize:%d fmt:%d\n",
    //  decoder->sampleRate,decoder->channel,decoder->sampleSize,decoder->pAudioCodecCtx->sample_fmt);
    FFLOGI("open acodec success! sampleRate:%d  channel:%d  fmt:%d\n",
           decoder->pAudioCodecCtx->sample_rate,decoder->pAudioCodecCtx->channels,decoder->pAudioCodecCtx->sample_fmt);
    return  ret;
}

static int i ;


void SeekFrame(AVFormatContext* context, int64_t seekFrame)//跳转到指定位置
{
    int defaultStreamIndex = av_find_default_stream_index(context);
//    if(ist->index == defaultStreamIndex)
//    {
    auto time_base = context->streams[defaultStreamIndex]->time_base;
    auto seekTime = context->streams[defaultStreamIndex]->start_time + av_rescale(seekFrame, time_base.den, time_base.num);
    int ret ;
//        if(seekTime > ist->cur_dts)
//        {
//            ret = av_seek_frame(context, defaultStreamIndex, seekTime, AVSEEK_FLAG_ANY);
//        }
//        else
//        {
    ret = av_seek_frame(context, -1, seekTime, AVSEEK_FLAG_ANY | AVSEEK_FLAG_BACKWARD);
//        }
//    }
}

int  Read(Decoder * decoder){


    //这里先不加线程锁，在启动多线程的地方统一加锁
    // AVPacket * pkt = malloc(sizeof(AVPacket));
    if (!decoder->pFormatCtx) {
        return -1;
    }

    int64_t time = (int64_t) (decoder->totleMs * 0.0001);

    int err = av_read_frame(decoder->pFormatCtx,&decoder->pkt);

    //定位到这个位置1000秒
    int64_t k = (int64_t) (1000 / av_q2d(decoder->pFormatCtx->streams[1]->time_base));
//    SeekFrame(decoder->pFormatCtx,100);
    if(i==100){
        int ret =   av_seek_frame(decoder->pFormatCtx, 1 , k, AVSEEK_FLAG_BACKWARD);

//    avcodec_flush_buffers(decoder->pVideoCodecCtx);
////
        FFLOGI("*** yang ret %d",ret);
        FFLOGI("*** yang ret %d",k);
        FFLOGI("*** yang i %d",i);
    }
    i++;
    FFLOGI("*** yang decoder->pFormatCtx %ld",decoder->pkt.pts);

    if (err != 0) {
        printError("av_read_pkt_error:",err);
        if (err == -541478725){
            //读取文件结束
            return -9;
        }
        return  -2;
    }
    return  0;
}



int Decode(Decoder* decoder,AVPacket packet){
    if (!decoder->pFormatCtx) {
        return -1;
    }
    //分配AVFream 空间
    if (decoder->pYuv == NULL) {
        decoder->pYuv = av_frame_alloc();
    }
    if (decoder->pPcm == NULL) {
        decoder->pPcm = av_frame_alloc();
    }
    AVCodecContext * pCodecCtx;
    AVFrame * tempFrame;








    if (packet.stream_index == decoder->videoStreamIndex) {
        pCodecCtx = decoder->pVideoCodecCtx;
        tempFrame = decoder->pYuv;
    }else if (packet.stream_index == decoder->audioStreamIndex){
        pCodecCtx = decoder->pAudioCodecCtx;
        tempFrame = decoder->pPcm;
    }else{
        return -1;
    }
    if (!pCodecCtx) {
        return -1;
    }
    int re = avcodec_send_packet(pCodecCtx,&packet);
    if (re != 0) {
        char errorBuf[1024] = {0};
        av_make_error_string(errorBuf,1024,re);
        FFLOGE("send pkt error:%s",errorBuf);
        return -1;
    }
    re = avcodec_receive_frame(pCodecCtx, tempFrame);
    if (re != 0) {
        char errorBuf[1024] = {0};
        av_make_error_string(errorBuf,1024,re);
        FFLOGE("receive av_data error:%s",errorBuf);
        return -1;
    }
    //解码后再获取pts  解码过程有缓存
    if (packet.stream_index == decoder->videoStreamIndex) {
        decoder->vFps = (int)((packet.pts * r2d(decoder->pFormatCtx->streams[decoder->videoStreamIndex]->time_base))*1000);
    }else if (packet.stream_index == decoder->audioStreamIndex){
        decoder->aFps = (int)((packet.pts * r2d(decoder->pFormatCtx->streams[decoder->audioStreamIndex]->time_base))*1000);
    }
    FFLOGI("[D]");
    return 0;
}
int Min(int a,int b){
    return a>b?b:a;
}
void copyDecodedFrame(unsigned char *src, unsigned char *dist,int linesize, int width, int height) {
    width = Min(linesize, width);
    if (sizeof(dist) == 0) {
        return;
    }
    for (int i = 0; i < height; ++i) {
        memcpy(dist, src, width);
        dist += width;
        src += linesize;
    }
}
int YuvToGLData(Decoder *decoder){
    if (!decoder->pFormatCtx || !decoder->pYuv ||decoder-> pYuv->linesize[0] <= 0) {
        return -1;
    }
    //把数据重新封装成opengl需要的格式
    unsigned int lumaLength= (unsigned int)(decoder->pYuv->height)*(Min(decoder->pYuv->linesize[0], decoder->pYuv->width));
    unsigned int chromBLength=(unsigned int)((decoder->pYuv->height)/2)*(Min(decoder->pYuv->linesize[1], (decoder->pYuv->width)/2));
    unsigned int chromRLength=(unsigned int)((decoder->pYuv->height)/2)*(Min(decoder->pYuv->linesize[2], (decoder->pYuv->width)/2));

    decoder->yuvFrame.luma.dataBuffer = (unsigned char*)malloc(lumaLength);
    decoder->yuvFrame.chromaB.dataBuffer = (unsigned char*)malloc(chromBLength);
    decoder->yuvFrame.chromaR.dataBuffer = (unsigned char*)malloc(chromRLength);

    decoder->yuvFrame.width=(unsigned int)decoder->pYuv->width;
    decoder->yuvFrame.height=(unsigned int)decoder->pYuv->height;

    if (decoder->pYuv->height <= 0) {
        free(decoder->yuvFrame.luma.dataBuffer);
        free(decoder->yuvFrame.chromaB.dataBuffer);
        free(decoder->yuvFrame.chromaR.dataBuffer);
        return -1;
    }

    copyDecodedFrame(decoder->pYuv->data[0],decoder->yuvFrame.luma.dataBuffer,decoder->pYuv->linesize[0],
                     decoder->pYuv->width,decoder->pYuv->height);
    copyDecodedFrame(decoder->pYuv->data[1], decoder->yuvFrame.chromaB.dataBuffer,decoder->pYuv->linesize[1],
                     decoder->pYuv->width / 2,decoder->pYuv->height / 2);
    copyDecodedFrame(decoder->pYuv->data[2], decoder->yuvFrame.chromaR.dataBuffer,decoder->pYuv->linesize[2],
                     decoder->pYuv->width / 2,decoder->pYuv->height / 2);
    return 0;
}
int ToPcm(Decoder * decoder,AACFrame * frame){
    if (!decoder->pFormatCtx || !decoder->pPcm) {
        return -1;
    }
    // printf("sample_rate:%d,channels:%d,sample_fmt:%d,channel_layout:%llu,nb_samples:%d\n",pAudioCodecCtx->sample_rate,pAudioCodecCtx->channels,pAudioCodecCtx->sample_fmt,pAudioCodecCtx->channel_layout,pPcm->nb_samples);
    //音频重采样
    if (decoder->pSwrCtx == NULL) {
        decoder->pSwrCtx = swr_alloc();
        swr_alloc_set_opts(decoder->pSwrCtx,
                           AV_CH_LAYOUT_STEREO,//2声道立体声
                           AV_SAMPLE_FMT_S16,  //采样大小 16位
                           decoder->sampleRate,
                           decoder->pAudioCodecCtx->channel_layout,
                           decoder->pAudioCodecCtx->sample_fmt,// 样本类型
                           decoder->pAudioCodecCtx->sample_rate,
                           0, 0);
        int ret = swr_init(decoder->pSwrCtx);
        char errorBuf[1024] = {0};
        av_make_error_string(errorBuf,1024,ret);
        FFLOGE("swr_init error:%s",errorBuf);
    }
    uint8_t * tempData[1];
    tempData[0] = (uint8_t*)frame->dataBuffer;
    int len = swr_convert(decoder->pSwrCtx, tempData, 10000, (const uint8_t**)decoder->pPcm->data,decoder->pPcm->nb_samples);
    if (len < 0) {
        char errbuf[1024] = {0};
        FFLOGE("swr_convert error:%d",len);
        return 0;
    }
    int outSize = av_samples_get_buffer_size(NULL,
                                             CHANNEL,
                                             len,
                                             AV_SAMPLE_FMT_S16,0);
    frame->length = (unsigned int)outSize;
    FFLOGI("nb_smples:%d,des_smples:%d,outSize:%d",decoder->pPcm->nb_samples,len,outSize);
    return 0;
}
void printError(const char * flag,int ret){
    char errorbuf[1024] = {0};
    av_make_error_string(errorbuf, 1024, ret);
    FFLOGE("%s,ret:%d,%s", flag, ret, errorbuf);
}
double r2d(AVRational r){
    return r.num == 0 || r.den == 0 ? 0.:(double)r.num/(double)r.den;
}