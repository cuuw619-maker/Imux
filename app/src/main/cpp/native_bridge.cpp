#include <jni.h>

#include "imux_abi.h"

extern "C" JNIEXPORT jlong JNICALL
Java_com_imux_game_NativeEngine_create(JNIEnv*, jobject) {
    return static_cast<jlong>(imux_engine_create());
}

extern "C" JNIEXPORT void JNICALL
Java_com_imux_game_NativeEngine_destroy(JNIEnv*, jobject, jlong handle) {
    imux_engine_destroy(static_cast<ImuxEngineHandle>(handle));
}

extern "C" JNIEXPORT void JNICALL
Java_com_imux_game_NativeEngine_resize(JNIEnv*, jobject, jlong handle, jint width, jint height) {
    imux_engine_resize(static_cast<ImuxEngineHandle>(handle), width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_imux_game_NativeEngine_update(JNIEnv*, jobject, jlong handle, jfloat deltaSeconds) {
    imux_engine_update(static_cast<ImuxEngineHandle>(handle), deltaSeconds);
}

extern "C" JNIEXPORT void JNICALL
Java_com_imux_game_NativeEngine_render(JNIEnv*, jobject, jlong handle) {
    imux_engine_render(static_cast<ImuxEngineHandle>(handle));
}

extern "C" JNIEXPORT void JNICALL
Java_com_imux_game_NativeEngine_pause(JNIEnv*, jobject, jlong handle) {
    imux_engine_pause(static_cast<ImuxEngineHandle>(handle));
}

extern "C" JNIEXPORT void JNICALL
Java_com_imux_game_NativeEngine_resume(JNIEnv*, jobject, jlong handle) {
    imux_engine_resume(static_cast<ImuxEngineHandle>(handle));
}
