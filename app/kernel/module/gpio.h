#ifndef GPIO_H
#define GPIO_H

#define INTERRUPT_PIN 31
#define OUTPUT_PIN 25

extern int interruptIRQ;

int gpio_init(void);
void gpio_exit(void);

#endif
