#ifndef NTLINK_CLIENT_H
#define NTLINK_CLIENT_H

int ntlink_client_init();
void ntlink_client_communicate(int nextDelay);
void ntlink_client_exit();

#endif
