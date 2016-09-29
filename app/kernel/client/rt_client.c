#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/mman.h>

#include "rt_client.h"

int fd = 0;

rt_data_t* rt_client_init()
{
    if (fd == 0)
    {
        fd = open("/proc/rtdma", O_RDWR);
        if (fd < 0)
        {
            perror("open");
            return NULL;
        }
    }

    rt_data_t* addr = (rt_data_t*) mmap(NULL, RT_PAGE_SIZE, PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);
    if (addr == MAP_FAILED)
    {
        perror("mmap");
        return NULL;
    }
    return addr;
}

void rt_client_exit()
{
    close(fd);
}
