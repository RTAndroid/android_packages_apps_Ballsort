#ifndef RT_DATA_H
#define RT_DATA_H

#define RT_PAGE_SIZE 4096

typedef struct
{
    int ball_count;
    int base_delay;
    int next_delay;
} rt_control_data;

typedef volatile unsigned int rt_data_t;

#endif
