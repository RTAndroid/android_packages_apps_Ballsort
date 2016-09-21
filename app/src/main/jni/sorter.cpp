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

#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include <sys/types.h>

#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Ballsort-JNI", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Ballsort-JNI", __VA_ARGS__)

int fd = 0;

extern "C" jboolean JNICALL Java_rtandroid_ballsort_hardware_Sorter_openMemory(JNIEnv* env, jobject obj)
{
    LOGI("Opening shared memory");

    // looks like we are already initialized
    if (fd != 0)
    {
        LOGE("The file descriptor is already initialized");
        return JNI_FALSE;
    }

    // map kernel memory to userspace
    fd = open("/proc/rtdma", O_RDWR);
    if (fd < 0)
    {
        LOGE("Failed to open rtdma proc entry. errno: %d", errno);
        return JNI_FALSE;
    }

    // we are good to go
    LOGI("File was opened");
    return JNI_TRUE;
}

extern "C" void JNICALL Java_rtandroid_ballsort_hardware_Sorter_setDelays(JNIEnv* env, jobject obj, jint baseDelay, jint nextDelay)
{
    char result[256];
    memset(result, 0, sizeof(result));
    sprintf(result, "%d,%d", baseDelay, nextDelay);

    write(fd, result, strlen(result));
}

extern "C" jint JNICALL Java_rtandroid_ballsort_hardware_Sorter_getBallCount(JNIEnv* env, jobject obj)
{
    char result[256];
    memset(result, 0, sizeof(result));

    read(fd, result, sizeof(result));
    return atoi(result);
}

extern "C" jboolean JNICALL Java_rtandroid_ballsort_hardware_Sorter_closeMemory(JNIEnv* env, jobject obj)
{
    LOGI("Closing shared memory");

    // looks like we are already initialized
    if (fd == 0)
    {
        LOGE("Operation failed: the file descriptor wasn't initialized");
        return JNI_FALSE;
    }

    // close the memory region
    close(fd);
    fd = 0;

    LOGI("Memory was closed");
    return JNI_TRUE;
}
