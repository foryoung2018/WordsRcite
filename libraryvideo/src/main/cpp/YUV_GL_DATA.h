//
//  YUV_GL_DATA.h
//
//
//  Created by huizai on 2017/10/26.
//  Copyright ? 2017Äê huizai. All rights reserved.
//

#ifndef YUV_GL_DATA_h
#define YUV_GL_DATA_h

#pragma pack(push, 1)

typedef struct H264FrameDef
{
	unsigned int    length;
	unsigned char*  dataBuffer;

}H264Frame;

typedef struct AACFrameDef
{
	int    sampleRate;
	int    sampleSize;
	int    channel;
	unsigned int    length;
	unsigned char*  dataBuffer;

}AACFrame;

typedef struct  H264YUVDef
{
	unsigned int    width;
	unsigned int    height;
	H264Frame       luma;
	H264Frame       chromaB;
	H264Frame       chromaR;
}H264YUV_Frame;


#pragma pack(pop)

#endif /* YUV_GL_DATA_h */