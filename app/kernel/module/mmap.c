#include <linux/mm.h>
#include <linux/module.h>
#include <linux/slab.h>

#include "../rt_data.h"
#include "mmap.h"

//! Global mmap object
struct mmap_info_t* info = NULL;

void mmap_open(struct vm_area_struct* vma) { }
void mmap_close(struct vm_area_struct* vma) { }

int mmap_load(struct vm_area_struct* vma, struct vm_fault* vmf)
{
    struct page* page;
    if (!info->data)
    {
        printk("RTDMA: MMAP failed to load!\n");
        return 1;
    }

    page = virt_to_page(info->data);
    get_page(page);
    vmf->page = page;

    return 0;
}

struct vm_operations_struct mmap_vm_ops =
{
    .open =   mmap_open,
    .close =  mmap_close,
    .fault =  mmap_load,
};

int memory_init(void)
{
    printk("RTDMA: MMAP init started\n");
    info = kmalloc(sizeof(struct mmap_info_t), GFP_KERNEL);
    info->data = (rt_data_t*) get_zeroed_page(GFP_KERNEL);
    printk("RTDMA: MMAP init finished\n");
    return 0;
}

void memory_exit(void)
{
    printk("RTDMA: MMAP exit started\n");
    free_page((unsigned long)info->data);
    kfree(info);
    printk("RTDMA: MMAP exit finished\n");
}
