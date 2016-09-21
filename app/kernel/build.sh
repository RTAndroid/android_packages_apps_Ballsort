#!/bin/bash

echo ""
echo "---------------------------------------"
echo "| Building rtdma.ko for Odroid XU4  |"
echo "---------------------------------------"
echo ""

MODULE_NAME="rtdma"
TARGET_ARCH="arm"
ARM_CROSS_COMPILE="CROSS_COMPILE=/media/android/platform/prebuilts/gcc/linux-x86/arm/arm-eabi-4.8/bin/arm-eabi-"
KERNEL_OUT="/media/android/platform/out/target/product/odroidxu3/obj/KERNEL_OBJ"

echo " * Building the kernel module..."
echo ""
make -C module $ARM_CROSS_COMPILE ARCH=$TARGET_ARCH KERNEL_DIR=$KERNEL_OUT
if [[ ! -f module/$MODULE_NAME.ko ]]; then
    echo "Error: module compilation failed!"
    exit 1
fi

echo ""
echo " * Building the client..."
echo ""
export PATH=$PATH:/media/android/android-ndk/standalone/bin
export CC=arm-linux-androideabi-gcc
make -C client $ARM_CROSS_COMPILE ARCH=$TARGET_ARCH KERNEL_DIR=$KERNEL_OUT
if [[ ! -f client/$MODULE_NAME ]]; then
    echo "Error: client compilation failed!"
    exit 1
fi

echo ""
echo " * Copying files..."
cp module/$MODULE_NAME.ko $MODULE_NAME.ko
cp module/$MODULE_NAME.ko ../src/main/res/raw/$MODULE_NAME.ko
cp client/$MODULE_NAME $MODULE_NAME

echo " * Cleaning up object files..."
echo ""
make -C module $ARM_CROSS_COMPILE ARCH=$TARGET_ARCH KERNEL_DIR=$KERNEL_OUT clean
rm client/$MODULE_NAME

remote="137.226.8.70"
echo ""
echo " * Pinging the board at $remote ..."
ping -q -c 1 $remote > /dev/null
if (($? != 0)); then
    echo "Error: no connection to $remote possible!"
    exit 1
fi

echo " * Starting ADB over network..."
adb kill-server > /dev/null
adb start-server > /dev/null
adb connect $remote > /dev/null
adb devices > /dev/null

echo " * Checking connected devices..."
DEVICES=$(adb devices)
if [[ "$DEVICES" != *"device"* ]] ; then
    echo ""
    echo "Error: no device in adb mode found"
    echo "Make sure the device is connected with debugging enabled"
    exit 1
fi

echo " * Pushing the module to the device..."
adb shell "rm -rf /data/local/tmp/*"
adb push $MODULE_NAME.ko /data/local/tmp/
adb push $MODULE_NAME /data/local/tmp/

echo "All done. Files were copied to /data/local/tmp"
