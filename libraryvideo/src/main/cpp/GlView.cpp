//
// Created by huizai on 2017/11/24.
//

#include "GlView.h"
#include "GlUtils.h"

typedef enum {
    TEXY = 0,
    TEXU,
    TEXV,
    TEXC
};

EGLContext         *glContext;
GLuint             renderBuffer;
GLuint             program;
GLuint             positionHandle,texCoord;
GLuint             textureYUV[3];
GLuint             videoW,videoH;
GLsizei            viewScale;

void initGL(){
    //glClearColor(0.1f, 0.4f, 0.6f, 1.0f);
    float data[] = {
            -0.2f,-0.2f,-0.6f,1.0f,
            0.2f,-0.2f,-0.6f,1.0f,
            0.0f,0.2f,-0.6f,1.0f
    };

    SetupYUVTextures();
    unsigned char* shaderCode = VsStr();
    GLuint vsShader = CompileShader(GL_VERTEX_SHADER,(char*)shaderCode);
  //  delete(shaderCode);
    shaderCode = FsStr();
    GLuint fsShader = CompileShader(GL_FRAGMENT_SHADER,(char*)shaderCode);
  //  delete(shaderCode);
    program = CreateProgram(vsShader,fsShader);
    glDeleteShader(vsShader);
    glDeleteShader(fsShader);
    positionHandle = (GLuint)glGetAttribLocation(program,"position");
    texCoord = (GLuint)glGetAttribLocation(program,"TexCoordIn");

    //像素数据对齐,第二个参数默认为4,一般为1或4
    glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
    //使用着色器
    glUseProgram(program);
    //获取一致变量的存储位置
    GLint textureUniformY = glGetUniformLocation(program, "SamplerY");
    GLint textureUniformU = glGetUniformLocation(program, "SamplerU");
    GLint textureUniformV = glGetUniformLocation(program, "SamplerV");
    //对几个纹理采样器变量进行设置
    glUniform1i(textureUniformY, 0);
    glUniform1i(textureUniformU, 1);
    glUniform1i(textureUniformV, 2);
    setVideoSize(0,0);
}

void SetupYUVTextures(){
    if(textureYUV[TEXY]){
        glDeleteTextures(3,textureYUV);
    }
    //生成纹理
    glGenTextures(3,textureYUV);
    if (!textureYUV[TEXY] || !textureYUV[TEXU] || !textureYUV[TEXV])
    {
        printf("<<<<<<<<<<<<纹理创建失败!>>>>>>>>>>>>");
        return;
    }
    //选择当前活跃单元
    glActiveTexture(GL_TEXTURE0);
    //绑定Y纹理
    glBindTexture(GL_TEXTURE_2D, textureYUV[TEXY]);
    //纹理过滤函数
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);//放大过滤
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);//缩小过滤
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);//水平方向
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);//垂直方向

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, textureYUV[TEXU]);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, textureYUV[TEXV]);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
}

void displayYUV420pData(H264YUV_Frame * frame){
    if (frame->width==0 || frame->height ==0)
        return;
    int w = frame->width;
    int h = frame->height;
    if (w != videoW || h != videoH){
        setVideoSize(frame->width,frame->height);
    }
    //绑定
    glBindTexture(GL_TEXTURE_2D,textureYUV[TEXY]);
    /**
     更新纹理
     @param target#>  指定目标纹理，这个值必须是GL_TEXTURE_2D。 description#>
     @param level#>   执行细节级别。0是最基本的图像级别，n表示第N级贴图细化级别 description#>
     @param xoffset#> 纹理数据的偏移x值 description#>
     @param yoffset#> 纹理数据的偏移y值 description#>
     @param width#>   更新到现在的纹理中的纹理数据的规格宽 description#>
     @param height#>  高 description#>
     @param format#>  像素数据的颜色格式, 不需要和internalformatt取值必须相同。可选的值参考internalformat。 description#>
     @param type#>    颜色分量的数据类型 description#>
     @param pixels#>  指定内存中指向图像数据的指针 description#>
     */
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (GLsizei)w, (GLsizei)h, GL_LUMINANCE, GL_UNSIGNED_BYTE, frame->luma.dataBuffer);
    glBindTexture(GL_TEXTURE_2D,textureYUV[TEXU]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (GLsizei)w/2, (GLsizei)h/2, GL_LUMINANCE, GL_UNSIGNED_BYTE, frame->chromaB.dataBuffer);
    glBindTexture(GL_TEXTURE_2D,textureYUV[TEXV]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (GLsizei)w/2, (GLsizei)h/2, GL_LUMINANCE, GL_UNSIGNED_BYTE, frame->chromaR.dataBuffer);
    //渲染
    Render();
}
void Render(){
    //把数据显示在这个视窗上
    /*
     我们如果选定(0, 0), (0, 1), (1, 0), (1, 1)四个纹理坐标的点对纹理图像映射的话，就是映射的整个纹理图片。如果我们选择(0, 0), (0, 1), (0.5, 0), (0.5, 1) 四个纹理坐标的点对纹理图像映射的话，就是映射左半边的纹理图片（相当于右半边图片不要了），相当于取了一张320x480的图片。但是有一点需要注意，映射的纹理图片不一定是“矩形”的。实际上可以指定任意形状的纹理坐标进行映射。下面这张图就是映射了一个梯形的纹理到目标物体表面。这也是纹理（Texture）比上一篇文章中记录的表面（Surface）更加灵活的地方。
     */
    glClearColor(0.0f,0.0f,0.0f,1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    static const GLfloat squareVertices[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f,
    };

    static const GLfloat coordVertices[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f,  0.0f,
            1.0f,  0.0f,
    };
    //更新属性值
    glVertexAttribPointer(positionHandle, 2, GL_FLOAT, 0, 0, squareVertices);
    //开启定点属性数组
    glEnableVertexAttribArray(positionHandle);

    glVertexAttribPointer(texCoord, 2, GL_FLOAT, 0, 0, coordVertices);
    glEnableVertexAttribArray(texCoord);

    //绘制
    //当采用顶点数组方式绘制图形时，使用该函数。该函数根据顶点数组中的坐标数据和指定的模式，进行绘制。
    //绘制方式,从数组的哪一个点开始绘制(一般为0),顶点个数
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
}

void setVideoSize(int width, int height){

    videoH = (GLuint)height;
    videoW = (GLuint)width;
    //开辟内存空间
    size_t length = (size_t)(width * height);
    void *blackDataY = malloc(length);
    void *blackDataU = malloc(length/4);
    void *blackDataV = malloc(length/4);
    if (blackDataY){
        /**
         对内存空间清零,作用是在一段内存块中填充某个给定的值，它是对较大的结构体或数组进行清零操作的一种最快方法
         @param __b#>   源数据 description#>
         @param __c#>   填充数据 description#>
         @param __len#> 长度 description#>
         @return <#return value description#>
         */
        memset(blackDataY, 0x0, length);
        memset(blackDataU, 0x0, length/4);
        memset(blackDataV, 0x0, length/4);
    }
    //绑定Y纹理
    glBindTexture(GL_TEXTURE_2D, textureYUV[TEXY]);
    /**
    根据像素数据,加载纹理
    @param target#>         指定目标纹理，这个值必须是GL_TEXTURE_2D。 description#>
    @param level#>          执行细节级别。0是最基本的图像级别，n表示第N级贴图细化级别 description#>
    @param internalformat#> 指定纹理中的颜色格式。可选的值有GL_ALPHA,GL_RGB,GL_RGBA,GL_LUMINANCE, GL_LUMINANCE_ALPHA 等几种。 description#>
    @param width#>          纹理的宽度 description#>
    @param height#>         高度 description#>
    @param border#>         纹理的边框宽度,必须为0 description#>
    @param format#>         像素数据的颜色格式, 不需要和internalformatt取值必须相同。可选的值参考internalformat。 description#>
    @param type#>           指定像素数据的数据类型。可以使用的值有GL_UNSIGNED_BYTE,GL_UNSIGNED_SHORT_5_6_5,GL_UNSIGNED_SHORT_4_4_4_4,GL_UNSIGNED_SHORT_5_5_5_1等。 description#>
    @param pixels#>         指定内存中指向图像数据的指针 description#>
    @return <#return value description#>
    */
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, blackDataY);
    //绑定U纹理
    glBindTexture(GL_TEXTURE_2D, textureYUV[TEXU]);
    //加载纹理
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width/2, height/2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, blackDataU);
    //绑定V数据
    glBindTexture(GL_TEXTURE_2D, textureYUV[TEXV]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width/2, height/2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, blackDataV);
    //释放malloc分配的内存空间
    free(blackDataY);
    free(blackDataU);
    free(blackDataV);
}
