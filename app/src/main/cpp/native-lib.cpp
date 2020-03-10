#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_wushaohong_bsdiffapp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" {
    extern int main(int argc, const char *argv[]);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wushaohong_bsdiffapp_MainActivity_bsPatch(JNIEnv *env, jobject thiz, jstring source_dir,
                                                   jstring output_file_path,
                                                   jstring patch_file_path) {

    const char * s = env->GetStringUTFChars(source_dir, 0);
    const char * o = env->GetStringUTFChars(output_file_path, 0);
    const char * p = env->GetStringUTFChars(patch_file_path, 0);

    const char *argv[] = {"", s, o, p};
    main(4, argv);

    env->ReleaseStringUTFChars(source_dir, s);
    env->ReleaseStringUTFChars(output_file_path, o);
    env->ReleaseStringUTFChars(patch_file_path, p);
}