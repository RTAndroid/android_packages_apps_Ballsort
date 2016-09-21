#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

char delays[] = {1, 2, 3, 4, 5, 6};

int main(int argc, char** argv)
{
    int fd = open("/proc/rtdma", O_RDWR);

    if(fd <= 0)
    {
        printf("Could not open file descriptor\n");
        return -1;
    }
    
    printf("Entering the loop...\n");
    while (1)
    {
        char result[256];
        memset(result, 0, sizeof(result));
        
        read(fd, result, sizeof(result));

        printf("Current ballcount: %s\n", result);

        // use random color for now
        int color = rand() % 7;
        int delay = delays[color];
        printf("Next delay: %d\n", delay);

        memset(result, 0, sizeof(result));
        sprintf(result, "%d,%d", 10, delay);
        
        write(fd, result, strlen(result));

        sleep(1);
    }

    return 0;
}
