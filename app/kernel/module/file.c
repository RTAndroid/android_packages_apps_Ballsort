#include <linux/proc_fs.h>
#include <linux/fs.h>
#include <linux/mm.h>
#include <linux/module.h>
#include <linux/miscdevice.h>
#include <linux/types.h>
#include <linux/uaccess.h>

MODULE_LICENSE("GPL");

#include "../rt_data.h"
#include "file.h"
#include "mmap.h"

#ifndef VM_RESERVED
#define VM_RESERVED (VM_DONTEXPAND | VM_DONTDUMP)
#endif

#define PERMISSIONS 0666

extern struct vm_operations_struct mmap_vm_ops;
extern struct mmap_info_t* info;


static int file_mmap(struct file* fd, struct vm_area_struct* vma)
{
    vma->vm_ops = &mmap_vm_ops;
    vma->vm_flags |= VM_RESERVED;
    mmap_open(vma);

    return 0;
}

static int file_close(struct inode* inode, struct file* fd)
{
    fd->private_data = NULL;
    return 0;
}

static int file_open(struct inode* inode, struct file* fd)
{
    fd->private_data = info;
    return 0;
}

static struct file_operations file_ops =
{
    .owner =   THIS_MODULE,
    .open =    file_open,
    .release = file_close,
    .mmap =    file_mmap,
};

int file_init(void)
{
    struct proc_dir_entry* config_file;

    printk("RTDMA: File init started\n");
    config_file = proc_create("rtdma", PERMISSIONS, NULL, &file_ops);
    if(!config_file)
    {
        printk("RTDMA: File init failed\n");
        return 1;
    }
    printk("RTDMA: File init finished\n");
    return 0;
}

void file_exit(void)
{
    printk("RTDMA: File exit started\n");
    remove_proc_entry("rtdma", NULL);
    printk("RTDMA: File exit finished\n");
}
