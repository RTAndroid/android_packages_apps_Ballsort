# Building
Issue 
make -B
to build "kernel" and "client".
You can build target "android" by pointing the kernel/Makefile to your android kernel tree.

# Howto
## file.c
Offeres a file in the debugfs that is used to access mmap.
Several operation handler are defined here.

## mmap.c
Allocates a kernelpage and exposes if mmap is called onto the debugfs file. 
If the wished page is not mapped it will fault. The faul handler will do the mapping

## thread.c
Main thread logic. The thread gets started when the debugfs file is opened and will be terminated when the filedescirtor is released.
The threadbody has to check if the mmap init happend before
