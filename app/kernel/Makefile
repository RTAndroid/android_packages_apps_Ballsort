
default: kernel client

kernel:
	$(MAKE) -C kernel

client: 
	gcc -std=c99 -Wall client/main.c client/rt_client.c -o rtdma_user -I.

clean:
	$(MAKE) -C kernel clean
	rm rtdma_user

android:
	$(MAKE) -C kernel android
	arm-linux-androideabi-gcc -std=c99 -fPIE -pie  -Wall client/main.c client/rt_client.c -o rtdma_user -I.

push: android
	adb shell ' mount -o rw,remount /system'
	adb push kernel/rtdma.ko /sdcard/rtdma.ko
	adb push rtdma_user /system/bin/rtdma_user
