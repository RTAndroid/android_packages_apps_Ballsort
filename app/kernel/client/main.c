#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <string.h>

#include "../rt_data.h"
#include "rt_client.h"
#include "ntlink_client.h"

char delays[] = {1, 2, 3, 4, 5, 6};

void sig_handler(int signo)
{
    rt_client_exit();
    ntlink_client_exit();
}

int main(int argc, char** argv)
{
    // register sig_handler
    if (signal(SIGTERM, sig_handler) == SIG_ERR)
    {
        printf("SIGTERM register failed\n");
        return -1;
    }

    // map kernel memory to userspace
    volatile rt_data_t* addr = rt_client_init();
    if (addr == NULL)
    {
        printf("DMA init failed\n");
        return -1;
    }

    // convert the address to our datastructure
    printf("Shared memory pointer: %x\n", (unsigned int) addr);
    volatile rt_control_data* data = (rt_control_data*) addr;
    data->base_delay = 1;

    // open the netlink connection
    ntlink_client_init();

    printf("Entering the loop...\n");
    while (1)
    {
        // save current state
        int count = data->ball_count;
        printf("Current ballcount: %d\n", count);

        // use random color for now
        int color = rand() % 7;
        int delay = delays[color];
        printf("Next delay: %d\n", delay);

        // send some data back and forth
        ntlink_client_communicate(delay);

        // wait for interrupt to appear / the ball to be handled
        printf("Waiting for the next ball...\n");
        data->next_delay = delay;
        while (data->ball_count == count ) { sleep(1); }
    }

    return 0;
}
