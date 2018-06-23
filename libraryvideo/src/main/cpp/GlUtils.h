//
// Created by huizai on 2017/11/28.
//

#ifndef FFMPEG_DEMO_GLUTILS_H
#define FFMPEG_DEMO_GLUTILS_H

#include "JniDefine.h"

class GlUtils {

};
GLuint CompileShader(GLenum shaderType, const char*shaderCode);
GLuint CreateProgram(GLuint vsShader,GLuint fsShader);
unsigned char* FsStr();
unsigned char* VsStr();
#endif //FFMPEG_DEMO_GLUTILS_H
