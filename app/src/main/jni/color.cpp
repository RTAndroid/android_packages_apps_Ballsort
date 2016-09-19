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
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>

#include <linux/i2c-dev.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>

#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Ballsort-JNI", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Ballsort-JNI", __VA_ARGS__)

#include "color.h"

// ----------------------

int i2cFD = -1;

void i2c_write(unsigned char* data, int length)
{
  int count = write(i2cFD, data, length);
  if (count < length) { LOGE("Writing failed. Sent bytes: %d/%d (errno: %d)", count, length, errno); }
}

void i2c_read(unsigned char* data, int length)
{
  int count = read(i2cFD, data, length);
  if (count < length) { LOGE("Reading failed. Received bytes: %d/%d (errno: %d)", count, length, errno); }
}

void i2c_command(unsigned char reg, unsigned char value)
{
  unsigned char wbuf[2];

  wbuf[0] = TCS34725_COMMAND_BIT | reg;
  wbuf[1] = value;

  i2c_write(wbuf, 2);
}

// ----------------------

extern "C" jboolean JNICALL Java_rtandroid_ballsort_hardware_ColorSensor_openI2C(JNIEnv* env, jobject obj)
{
    i2cFD = open(I2CBUS, O_RDWR);
    if (i2cFD < 0)
    {
        LOGE("Open color sensor at I2C device %s failed", I2CBUS);
        return JNI_FALSE;
    }

    jint status = ioctl(i2cFD, I2C_SLAVE, TCS34725_ADDRESS);
    if (status < 0)
    {
        LOGE("Failed to ioctl on color I2C at %s", I2CBUS);
        close(i2cFD);
        return JNI_FALSE;
    }

    // Set integration time
    i2c_command(TCS34725_ATIME, TCS34725_INTEGRATIONTIME_154MS);
    // Set gain
    i2c_command(TCS34725_CONTROL, TCS34725_GAIN_1X);

    // Enable
    i2c_command(TCS34725_ENABLE, TCS34725_ENABLE_PON);
    usleep(3000);
    i2c_command(TCS34725_ENABLE, TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN);

    LOGI("Opened I2C at %s. File descriptor: %d, status: %d", I2CBUS, i2cFD, status);
    return JNI_TRUE;
}

extern "C" jlong JNICALL Java_rtandroid_ballsort_hardware_ColorSensor_readI2C(JNIEnv* env, jobject obj)
{
    if (i2cFD < 0)
    {
        LOGE("Can't read from closed I2C");
        return 0;
    }

    uint8_t rgb[6];
    uint8_t wbuf[1] = { TCS34725_COMMAND_BIT | TCS34725_RDATAL };

    i2c_write(wbuf, 1);
    i2c_read(rgb, 6);

    jlong r = (rgb[1] << 8) | rgb[0];
    jlong g = (rgb[3] << 8) | rgb[2];
    jlong b = (rgb[5] << 8) | rgb[4];

    jlong value = (r << 32) | (g << 16) | b;
    return value;
}

extern "C" jboolean JNICALL Java_rtandroid_ballsort_hardware_ColorSensor_closeI2C(JNIEnv* env, jobject obj)
{
    jint status = close(i2cFD);
    if (status < 0)
    {
        LOGE("Closing I2C failed");
        return JNI_FALSE;
    }

    LOGI("Closed I2C file descriptor");
    return JNI_TRUE;
}
