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
#include <signal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#include <linux/netlink.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/mman.h>

#include "rt_data.h"

#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Ballsort-JNI", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Ballsort-JNI", __VA_ARGS__)

int fd = 0;
volatile rt_control_data* data;

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
        LOGE("Failed to open rtdma debug filesystem. errno: %d", errno);
        return JNI_FALSE;
    }

    // try to get access to that memory
    data = (rt_control_data*) mmap(NULL, RT_PAGE_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (data == NULL || data == MAP_FAILED)
    {
        LOGE("Failed to mmap the control data. errno: %d", errno);
        return JNI_FALSE;
    }

    // init delays
    data->base_delay = 0;
    data->next_delay = 0;
    data->ball_count = 0;

    // we are good to go
    LOGI("Memory was opened");
    return JNI_TRUE;
}

extern "C" void JNICALL Java_rtandroid_ballsort_hardware_Sorter_setDelays(JNIEnv* env, jobject obj, jint baseDelay, jint nextDelay)
{
    data->base_delay = baseDelay;
    data->next_delay = nextDelay;
}

extern "C" jint JNICALL Java_rtandroid_ballsort_hardware_Sorter_getBallCount(JNIEnv* env, jobject obj)
{
    return data->ball_count;
}

extern "C" void JNICALL Java_rtandroid_ballsort_hardware_Sorter_resetBallCount(JNIEnv* env, jobject obj)
{
    data->ball_count = 0;
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
}
