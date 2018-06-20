# WordsRcite

### 拆解背词，随时随地听单词

--------------------------------------------------------------------------------

### ---2018/5/23
//TODO  Fragmentation   https://github.com/YoKeyword/Fragmentation.git  待加入

--------------------------------------------------------------------------------

### ---2018/5/24
//TODO  RetrofitDemo   https://github.com/Carson-Ho/RetrofitDemo.git 待加入

--------------------------------------------------------------------------------

### ---2018/5/25
//TODO  RxAndroid   待加入

--------------------------------------------------------------------------------

### ---2018/5/29
加入有道翻译接口
加入HttpLoggingInterceptor  RxAndroid
```
OkHttpClient client = new OkHttpClient();
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(Level.BASIC);
client.interceptors().add(logging);

/* 可以通过 setLevel 改变日志级别
 共包含四个级别：NONE、BASIC、HEADER、BODY

NONE 不记录

BASIC 请求/响应行
--> POST /greeting HTTP/1.1 (3-byte body)
<-- HTTP/1.1 200 OK (22ms, 6-byte body)

HEADER 请求/响应行 + 头

--> Host: example.com
Content-Type: plain/text
Content-Length: 3

<-- HTTP/1.1 200 OK (22ms)
Content-Type: plain/text
Content-Length: 6

BODY 请求/响应行 + 头 + 体
*/

// 可以通过实现 Logger 接口更改日志保存位置
HttpLoggingIntercetptor logging = new HttpLoggingInterceptor(new Logger() {
    @Override
    public void log(String message) {
        Timber.tag("okhttp").d(message);
    }
});
```

//TODO插件化，待加入

--------------------------------------------------------------------------------

### ---2018/5/30

//TODO  video 音视频

### ---2018/5/31

//TODO C 编解码

### ---2018/6/11
加入APT
>AOP(一)  
APT
https://blog.csdn.net/trinity2015/article/details/80646582
@AttachView 注解 inject调用 toast方法


libraryone 调试 dialog

//TODO 音视频

### ---2018/6/12
加入ndk

hello-jni in libraryvideo

 NDK 接入步骤：
1.在app目录里面新建一个CMakeLists.txt文件，注意名称，添加如下代码：
```
cmake_minimum_required(VERSION 3.4.1)
include_directories(
    ${CMAKE_SOURCE_DIR}/src/main/cpp/include #h文件目录
)
add_library( # Sets the name of the library.
             jni-lib                        #c/cpp代码将要编译成为so库的名称，java代码加载库文件要用这个名称
             SHARED
             src/main/cpp/hello-cjni.c      #c代码文件路径
             src/main/cpp/hello-cppjni.cpp  #cpp代码文件路径 这里可以随意添加c、c++文件
              )
target_link_libraries( # Specifies the target library.
                       jni-lib
                       )
```
2.build.gradle 里面加入
```groovy
 externalNativeBuild {
        cmake {
            path "CMakeLists.txt"
        }
    }
```
3.Activity里面使用
```
Toast.makeText(this,stringFromJNI(),Toast.LENGTH_LONG).show();
public native String  stringFromJNI();
 static {
        System.loadLibrary("jni-lib");
    }
```

加入ffmpeg so 动态库

```groovy
cmake_minimum_required(VERSION 3.4.1)

include_directories(

                          ${CMAKE_SOURCE_DIR}/src/main/cpp/include #h文件目录
                           ${CMAKE_SOURCE_DIR}/libs/include
)
add_library( # Sets the name of the library.
             jni-lib                        #c/cpp代码将要编译成为so库的名称，java代码加载库文件要用这个名称
             SHARED
             src/main/cpp/hello-jni.cpp      #c代码文件路径
             # src/main/cpp/hello-cppjni.cpp  #cpp代码文件路径 这里可以随意添加c、c++文件
              )

  add_library(
              avcodec
              SHARED
              IMPORTED
              )
  add_library(
              avfilter
              SHARED
              IMPORTED
               )
  add_library(
              avformat
              SHARED
              IMPORTED
              )
  add_library(
              avutil
              SHARED
              IMPORTED
              )
  add_library(
              swresample
              SHARED
              IMPORTED
              )
  add_library(
              swscale
              SHARED
              IMPORTED
              )

#add_library(
#            fdk-aac
#            SHARED
#            IMPORTED
#            )
target_link_libraries( # Specifies the target library.
                       jni-lib
                       )

                       set_target_properties(
                           avcodec
                           PROPERTIES IMPORTED_LOCATION
                           ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavcodec.so
                           )
                       set_target_properties(
                               avfilter
                               PROPERTIES IMPORTED_LOCATION
                               ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavfilter.so
                               )
                       set_target_properties(
                                   avformat
                                   PROPERTIES IMPORTED_LOCATION
                                   ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavformat.so
                                   )
                       set_target_properties(
                                   avutil
                                   PROPERTIES IMPORTED_LOCATION
                                   ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libavutil.so
                                   )
                       set_target_properties(
                                   swresample
                                   PROPERTIES IMPORTED_LOCATION
                                   ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libswresample.so
                                    )
                       set_target_properties(
                                   swscale
                                   PROPERTIES IMPORTED_LOCATION
                                   ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libswscale.so
                                    )
                    #   set_target_properties(
                    #               fdk-aac
                    #               PROPERTIES IMPORTED_LOCATION
                    #               ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libfdk-aac.so
                    #                )
                       find_library( # Sets the name of the path variable.
                                     log-lib
                                     # Specifies the name of the NDK library that
                                     # you want CMake to locate.
                                     log )
                       target_link_libraries( # Specifies the target library.
                                            jni-lib
                                        #    fdk-aac
                                            avcodec
                                            avfilter
                                            avformat
                                            avutil
                                            swresample
                                            swscale
                                            ${log-lib}#这个是打印jni调试log要用到的库文件这里添加进来，最后打印视频时长就是用这个库打印
                                            )

```

//TODO https://blog.csdn.net/m0_37677536/article/details/78561085


### ---2018/6/14

加入opengles

https://developer.android.com/training/graphics/opengl/projection

响应点击事件
https://developer.android.com/training/graphics/opengl/touch

### ---2018/6/15

加入opengles opensles ffmpeg demo
https://blog.csdn.net/m0_37677536/article/details/78775007

**opengl 绘制YUV** 

>步骤：
>编写shader->编译shader->链成gpu程序（代码中的program）->分别创建yuv纹理对象->找到yuv纹理对象对应的显卡插槽（也就是要给gpu中运行的纹理对象传数据的地址）->给yuv纹理对象绑定数据->绘图。
>shaderCode  CompileShader CreateProgram UseProgram SetupYUVTextures   SetupYUVTextures


//TODO 显示进度条，水印，缩放，旋转等功能

--------------------------------------------------------------------------------

### ---2018/6/20

加入aspectj 注解AOP
https://blog.csdn.net/trinity2015/article/details/80739580

//TODO ijkplayer 功能解析

--------------------------------------------------------------------------------


### ---2018/6/21

加入javaassist 本地maven 
后续javaassit 功能参照  T-MVP
https://www.jianshu.com/p/dca3e2c8608a?from=timeline

仓库参照
http://www.jcodecraeer.com/a/anzhuokaifa/Android_Studio/2015/0227/2502.html

//TODO ijkplayer 功能解析
--------------------------------------------------------------------------------

