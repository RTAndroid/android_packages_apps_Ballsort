# Copyright (C) 2017 RTAndroid Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)

##################################################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform

LOCAL_JAVA_LIBRARIES := \
    framework

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-common \
    cmfm-android-support-v4 \
    cmfm-android-support-v7-appcompat \
    cmfm-android-support-design

LOCAL_PACKAGE_NAME := Ballsort
LOCAL_JNI_SHARED_LIBRARIES := libbenchmark
LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main/java)

appcompat_dir := ../../../external/android/support-prebuilt/appcompat/res
supportdesign_dir := ../../../external/android/support-prebuilt/support-design/res
res_dirs := app/src/main/res $(appcompat_dir) $(supportdesign_dir)
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.design

include $(BUILD_PACKAGE)

##################################################

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    cmfm-android-support-v4:../../../external/android/support-prebuilt/appcompat/android-support-v4.jar \
    cmfm-android-support-v7-appcompat:../../../external/android/support-prebuilt/appcompat/android-support-v7-appcompat.jar \
    cmfm-android-support-design:../../../external/android/support-prebuilt/support-design/android-support-design.jar

include $(BUILD_MULTI_PREBUILT)

##################################################

include $(call all-makefiles-under, $(LOCAL_PATH))
