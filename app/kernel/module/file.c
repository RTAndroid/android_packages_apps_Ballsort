#include <linux/proc_fs.h>
#include <linux/fs.h>
#include <linux/mm.h>
#include <linux/module.h>
#include <linux/miscdevice.h>
#include<linux/slab.h>
#include <linux/types.h>
#include <linux/uaccess.h>

MODULE_LICENSE("GPL");

#include "file.h"

#define PERMISSIONS 0666
#define OFFSET_EOF     1

int base_delay = 0;
int next_delay = 0;
int ball_count = 0;
    
static int get_next_var(char** buf)
{
    char *temp;
    char *delim = ",";

    temp = strsep(buf, delim);
    if (temp == NULL) { return 0; }

    return simple_strtoul(temp, NULL, 0);
}

static ssize_t file_write(struct file *f, const char *buffer, size_t count, loff_t *offset)
{
    char *buf;
    char **buf_ptr;

    if (count < 1)
    {
        printk("RTDMA: no arguments supplied, action aborted\n");
        return -EINVAL;
    }

    buf = kmalloc(count, GFP_KERNEL);
    buf_ptr = &buf;
    if (!buf)
    {
        printk(KERN_INFO "RTDMA: memory allocation failed\n");
        return -ENOMEM;
    }

    if (copy_from_user(buf, buffer, count))
    {
        kfree(buf);
        printk(KERN_INFO "RTDMA: data copy failed\n");
        return -EFAULT;
    }

    base_delay = get_next_var(buf_ptr);
    next_delay = get_next_var(buf_ptr);
    
    printk(KERN_INFO "RTDMA: Set delays to %d %d\n", base_delay, next_delay);
    

    kfree(buf);
    return count;
}

static ssize_t file_read(struct file *filep, char *buffer, size_t len, loff_t *offset)
{
    char result[256];
    memset(result, 0, sizeof(result));
    
    sprintf(result, "%d", ball_count);
    
    if (copy_to_user(buffer, result, strlen(result) + 1))
    {
        printk(KERN_INFO "RTDMA: copying data to user failed\n");
        return -EFAULT;
    }
    
    // Next read will lead to EOF
    return strlen(result) + 1;
}

static struct file_operations file_ops =
{
    .owner =   THIS_MODULE,
    .write =   file_write,
    .read =    file_read,
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
