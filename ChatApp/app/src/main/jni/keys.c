#include <jni.h>
JNIEXPORT jstring JNICALL
Java_com_example_chatapp_ChatActivity_getSecretKey(JNIEnv *env, jobject instance) {
return (*env)-> NewStringUTF(env, "RFombJom7HQ4pwt16zolgnODzxoXzCmVjauozynQQNk=");
}

JNIEXPORT jstring JNICALL
Java_com_example_chatapp_ChatActivity_getSecretIV(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "oVsaa9EUWyD0u9Ny");
}

