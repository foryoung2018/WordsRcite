cmake_minimum_required(VERSION 3.4.1)

find_path(GLES2_INCLUDE_DIR GLES2/gl2.h
    HINTS ${ANDROID_NDK})              #找到opengl头文件路径
find_library(GLES2_LIBRARY libGLESv2.so
    HINTS ${GLES2_INCLUDE_DIR}/../lib) #找到opengl库文件路径
find_path(SL_INCLUDE_DIR SLES/OpenSLES.h
    HINTS ${ANDROID_NDK})              #找到opensl头文件路径
find_library(SL_LIBRARY libOpenSLES.so
    HINTS ${SL_INCLUDE_DIR}/../lib)    #找到opensl库文件路径

include_directories(
     ${CMAKE_SOURCE_DIR}/src/main/cpp   #此处忽略
     ${CMAKE_SOURCE_DIR}/libs/include   #此处忽略
     ${GLES2_INCLUDE_DIR}               #把头文件路径添加进来
     ${SL_INCLUDE_DIR}                  #把头文件路径添加进来
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
        ${SL_LIBRARY}     #把opensl库文件添加进来
        ${GLES2_LIBRARY}  #把opengl库文件添加进来
        ${log-lib}#这个是打印jni调试log要用到的库文件这里添加进来，最后打印视频时长就是用这个库打印
)
