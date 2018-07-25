cmake_minimum_required(VERSION 3.4.1)

include_directories(
     ${CMAKE_SOURCE_DIR}/libs/include   #此处忽略
)

find_library(
              log-lib
              log )

add_library(push
            SHARED
            src/main/cpp/push.c      #c代码文件路径
            src/main/cpp/queue.c      #c代码文件路径
            )

add_library(
            x264
            STATIC
            IMPORTED
            )

set_target_properties(
              x264
              PROPERTIES IMPORTED_LOCATION
              ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libx264.a
              )

add_library(
            faac
            STATIC
            IMPORTED
            )

set_target_properties(
              faac
              PROPERTIES IMPORTED_LOCATION
              ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libfaac.a
              )

add_library(
            rtmp
            STATIC
            IMPORTED
            )

set_target_properties(
              rtmp
              PROPERTIES IMPORTED_LOCATION
              ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/librtmp.a
              )


include_directories(libs/include)

target_link_libraries( # Specifies the target library.
        push
        x264
        faac
        rtmp
        android
        ${log-lib}
)