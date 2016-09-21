#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>


#include "file.h"
#include "gpio.h"

//! Module enty point
static int __init rtdma_init(void)
{
    printk("RTDMA: loading\n");

    if (file_init() < 0)
    {
        printk("RTDMA: file_init failed\n");
        file_exit();
        return -EIO;
    }

    if (gpio_init() < 0)
    {
        printk("RTDMA: gpio_init failed\n");
        gpio_exit();
        file_exit();
        return -EIO;
    }

    printk("RTDMA: loaded\n");
    return 0;
}

//! Module exit point
static void __exit rtdma_exit(void)
{
    printk("RTDMA: exiting\n");

    gpio_exit();
    file_exit();

    printk("RTDMA: exited\n");
}

module_init(rtdma_init);
module_exit(rtdma_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Igor Kalkov, Maximilian Schander");
