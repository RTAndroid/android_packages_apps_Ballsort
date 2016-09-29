#include <linux/module.h>
#include <linux/netlink.h>
#include <linux/skbuff.h>
#include <net/sock.h>

#include "../rt_data.h"
#include "mmap.h"
#include "ntlink.h"

#define NETLINK_USER 31

extern struct mmap_info_t* info;
struct sock* ntlinkSocket;

static void recieve(struct sk_buff *input)
{
    char* payloadPointer;

    if (info == NULL || info->data == NULL)
    {
        printk("RTDMA: no shared memory available\n");
        return;
    }

    // put header around raw input
    struct nlmsghdr* inputHeader = (struct nlmsghdr*) input->data;
    payloadPointer = (char*) nlmsg_data(inputHeader);

    // save it to the shared memory
    volatile rt_control_data* rtdata = (rt_control_data*) info->data;
    rtdata->next_delay = *payloadPointer;
    printk("RTDMA: ntlink has received the next delay: %d\n", rtdata->next_delay);

    // socket buffer for output
    int responseLength = sizeof(int);
    struct sk_buff* skbOut = nlmsg_new(responseLength, 0);
    if (!skbOut)
    {
        printk("RTDMA: can't allocate the socket buffer\n");
        return;
    }

    // build the header around response
    struct nlmsghdr* outputHeader = nlmsg_put(skbOut, 0, 0, NLMSG_DONE, responseLength, 0);
    payloadPointer = (char*) nlmsg_data(outputHeader);
    *payloadPointer = rtdata->ball_count;

    int pid = inputHeader->nlmsg_pid;
    int ret = nlmsg_unicast(ntlinkSocket, skbOut, pid);
    if (ret < 0) { printk("RTDMA: sending the current ball count via ntlink failed!\n"); }
}

static struct netlink_kernel_cfg ntlink_cfg =
{
    .input = recieve,
};

int ntlink_init(void)
{
    printk("RTDMA: NTLINK init started\n");

    ntlinkSocket = netlink_kernel_create(&init_net, NETLINK_USER, &ntlink_cfg);
    if (!ntlinkSocket)
    {
        printk(KERN_ALERT "Error creating NTLINK socket.\n");
        return -1;
    }

    printk("RTDMA: NTLINK init finished\n");
    return 0;
}

void ntlink_exit(void) 
{
    printk("RTDMA: NTLINK exit started\n");
    netlink_kernel_release(ntlinkSocket);
    printk("RTDMA: NTLINK exit finished\n");
}
