cmake_minimum_required(VERSION 3.22.1)
project(imux LANGUAGES CXX)

add_library(imux SHARED
    native_bridge.cpp
    imux_abi.cpp
    engine.cpp
    core/logger.cpp
    renderer/renderer.cpp
)

target_include_directories(imux PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

target_compile_features(imux PRIVATE cxx_std_20)

target_compile_options(imux PRIVATE
    -Wall
    -Wextra
    -Wpedantic
    -Wconversion
    -Wshadow
)

find_library(log-lib log)

target_link_libraries(imux PRIVATE ${log-lib})
