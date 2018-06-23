//
// Created by huizai on 2017/11/28.
//


#include "GlUtils.h"

GLuint CreateBufferObject(GLenum bufferType, GLsizeiptr size, GLenum usage, void*data /* = nullptr */){
    GLuint object;
    glGenBuffers(1, &object);
    glBindBuffer(bufferType, object);
    glBufferData(bufferType, size, data, usage);
    glBindBuffer(bufferType, 0);
    return object;
}
GLuint CompileShader(GLenum shaderType, const char*shaderCode){
    GLuint shader = glCreateShader(shaderType);
    if (shader == 0){
        printf("glCreateShader fail\n");
        return 0;
    }
    if (shaderCode == nullptr){
        glDeleteShader(shader);
        return 0;
    }
    glShaderSource(shader, 1, &shaderCode, nullptr);
    glCompileShader(shader);
    GLint compileResult = GL_TRUE;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compileResult);
    if (compileResult == GL_FALSE){
        char errorBuf[1024] = { 0 };
        GLsizei logLen = 0;
        glGetShaderInfoLog(shader, 1024, &logLen, errorBuf);
        printf("Compile Shader fail error log : %s \nshader code :\n%s\n", logLen, shaderCode);
        glDeleteShader(shader);
        shader = 0;
    }
   // delete shaderCode;
    return shader;
}
GLuint CreateProgram(GLuint vsShader,GLuint fsShader){
    GLuint  program = glCreateProgram();
    glAttachShader(program,vsShader);
    glAttachShader(program,fsShader);
    glLinkProgram(program);
    glDetachShader(program,vsShader);
    glDetachShader(program,fsShader);
    GLint ret;
    glGetProgramiv(program,GL_LINK_STATUS,&ret);
    if (ret == GL_FALSE){
        char errorBuf[1024] = {0};
        GLsizei writed = 0;
        glGetProgramInfoLog(program,1024,&writed,errorBuf);
        printf("create gpu program fail,link error:%s\n",errorBuf);
        glDeleteProgram(program);
        program = 0;
    }
    return program;
}


unsigned char* FsStr(){
    const char* fsstr = "varying lowp vec2 TexCoordOut;\
             uniform sampler2D SamplerY;\
             uniform sampler2D SamplerU;\
             uniform sampler2D SamplerV;\
             void main(void)\
            {\
            mediump vec3 yuv;\
            lowp vec3 rgb;\
            yuv.x = texture2D(SamplerY, TexCoordOut).r;\
            yuv.y = texture2D(SamplerU, TexCoordOut).r - 0.5;\
            yuv.z = texture2D(SamplerV, TexCoordOut).r - 0.5;\
            rgb = mat3( 1,       1,         1,\
            0,       -0.39465,  2.03211,\
            1.13983, -0.58060,  0) * yuv;\
            gl_FragColor = vec4(rgb, 1);\
            }";
    return (unsigned char*)fsstr;
}
unsigned char* VsStr(){
    const char* vsstr = "attribute vec4 position;\
            attribute vec2 TexCoordIn;\
            varying vec2 TexCoordOut;\
            void main(void)\
            {\
            gl_Position = position;\
            TexCoordOut = TexCoordIn;\
            }";
    return (unsigned char*)vsstr;
}
unsigned char* TestFs(){
    const char* fsstr = "#ifdef GL_ES\
            precision mediump float;\
            #endif\
            void main(void)\
            {\
                gl_FragColor=vec4(1.0,1.0,1.0,1.0);\
            }";
    return (unsigned char*)fsstr;
}
unsigned char* TestVs(){
    const char* vsstr = "attribute vec4 position;\
                  void main(void)\
            {\
            gl_Position = position;\
            }";
    return (unsigned char*)vsstr;
}