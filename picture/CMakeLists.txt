# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)


include_directories(
     ${CMAKE_SOURCE_DIR}/libs/x264/include
)

add_library(
             push
             SHARED
             src/main/cpp/push.cpp )

find_library(
              log-lib
              log )


# set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../libs)

add_library( x264
             SHARED
             IMPORTED )
set_target_properties( x264
                       PROPERTIES IMPORTED_LOCATION
                       ../../../../libs/x264/armeabi-v7a/libavcodec.so )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

  add_library(
              avcodec
              SHARED
              IMPORTED
              )

set_target_properties(
avcodec
PROPERTIES IMPORTED_LOCATION
../../../../libs/x264/armeabi-v7a/libavcodec.so
)

include_directories(libs/include)

#target_include_directories(push PRIVATE libs/include)

target_link_libraries( push log android avcodec
                       ${log-lib} )
