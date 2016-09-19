#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <linux/netlink.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <errno.h>

#include "ntlink_client.h"

#define MESSAGE_SIZE 16
#define NETLINK_USER 31 // socket protocol, can this be found in some header?

int sock = -1;
struct sockaddr_nl clientAddr;
struct sockaddr_nl kernelAddr;

int ntlink_client_init(void)
{
    printf("Opening ntlink socket\n");

    sock = socket(PF_NETLINK, SOCK_RAW, NETLINK_USER);
    if (sock < 0)
    {
        printf("Socket initialization failed!\n");
        return -1;
    }

    memset(&clientAddr, 0, sizeof(clientAddr));
    memset(&kernelAddr, 0, sizeof(kernelAddr));

    // fill sockaddr_nl struct for user
    clientAddr.nl_family = AF_NETLINK;
    clientAddr.nl_pid = getpid();

    // fill sockaddr_nl struct for kernel, nl_pid stays 0 for kernel
    kernelAddr.nl_family = AF_NETLINK;

    // ready to go
    bind(sock, (struct sockaddr*) &clientAddr, sizeof(clientAddr));

    return 0;
}

void ntlink_client_exit(void)
{
    printf("Closing ntlink socket\n");

    close(sock);
    sock = -1;
}

void ntlink_client_communicate(int nextDelay)
{
    if (sock < 0)
    {
        printf("Can't communicate with a closed socket!\n");
        return;
    }

    // create the header
    int packageLength = NLMSG_SPACE(MESSAGE_SIZE);
    struct nlmsghdr* header = (struct nlmsghdr*) malloc(packageLength);
    memset(header, 0, packageLength);
    header->nlmsg_len = packageLength;
    header->nlmsg_pid = getpid();

    // IO buffer control structure
    struct iovec iov = { 0 };
    iov.iov_base = (void*) header;
    iov.iov_len = header->nlmsg_len;

    // socket message header
    struct msghdr msg = { 0 };
    msg.msg_name = (void*) &kernelAddr;
    msg.msg_namelen = sizeof(kernelAddr);
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;

    // use the pointer to the payload associated with the passed nlmsghdr
    char* payloadPointer = (char*) NLMSG_DATA(header);
    *payloadPointer = nextDelay;

    // send the next delay
    printf("Sending next delay of %d to the kernel...\n", nextDelay);
    int nwrite = sendmsg(sock, &msg, 0);
    if (nwrite < 0)
    {
        // probably the call failed
        printf("Failed to send a message (errno: %d)\n", errno);
    }
    usleep(10);

    // recieve the last ball count
    printf("Awaiting a message from the kernel...\n");
    int nread = recvmsg(sock, &msg, MSG_DONTWAIT);
    if (nread < 0)
    {
        // looks there is no data yet
        printf("Failed to receive a message (errno: %d)\n", errno);
    }
    else
    {
        // show the received data
        payloadPointer = (char*) NLMSG_DATA(header);
        char count = *payloadPointer;
        printf("Received ball count of %d\n", count);
    }

    // cleanup before leaving
    free(header);
}
