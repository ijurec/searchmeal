#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_searchmeal_utilities_NetworkUtil_invokeNativeFunction(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("");
}