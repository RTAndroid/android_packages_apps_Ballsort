/*
 * Copyright (C) 2016 RTAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <unistd.h>

#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Ballsort-delay-JNI", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Ballsort-delay-JNI", __VA_ARGS__)


extern "C" void JNICALL Java_rtandroid_ballsort_util_Utils_nativeSleepUs(JNIEnv* env, jobject obj, jint usec)
{
    usleep(usec);
}