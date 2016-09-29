#ifndef MMAP_H
#define MMAP_H

//! custom mmap structure
struct mmap_info_t
{
	rt_data_t* data;
};

int memory_init(void);
void memory_exit(void);
void mmap_open(struct vm_area_struct* vma);

#endif
