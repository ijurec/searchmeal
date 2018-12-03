#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_searchmeal_SearchMealApp_invokeNativeFunction(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("");
}