#include <linux/gpio.h>
#include <linux/interrupt.h>
#include <linux/delay.h>
#include <linux/slab.h>
#include <linux/module.h>

#include "gpio.h"

int interruptIRQ;

extern int base_delay;
extern int next_delay;
extern int ball_count;

int initPin(int pinID)
{
    if (!gpio_is_valid(pinID))
    {
        printk("RTDMA: The requested GPIO pin %d is not available\n", pinID);
        return 1;
    }

    if (gpio_request(pinID, "GPIO pin"))
    {
        printk("RTDMA: Unable to request GPIO pin %d\n", pinID);
        return 1;
    }

    printk("RTDMA: Requested pin %d\n", pinID);
    return 0;
}

static irqreturn_t gpio_isr(int irq, void* data)
{
    if (irq != interruptIRQ)
    {
        printk("RTDMA: detected unhandled interrupt\n");
        return IRQ_NONE;
    }

    // step 1: base delay
    mdelay(base_delay);

    // step 2: open the valve
    gpio_set_value(OUTPUT_PIN, 1);
    udelay(next_delay);
    gpio_set_value(OUTPUT_PIN, 0);
    ball_count++;

    printk("RTDMA: interrupt handled (base=%dms)(time=%dus)\n", base_delay, next_delay);
    return IRQ_HANDLED;
}

int gpio_init(void)
{
    int ret;
    printk("RTDMA: GPIO init started\n");

    if (initPin(OUTPUT_PIN))
    {
        printk(KERN_ERR "RTDMA: Failed to initialize pin %d\n", OUTPUT_PIN);
        return 1;
    }

    if (gpio_direction_output(OUTPUT_PIN, 0) < 0)
    {
        printk(KERN_ERR "RTDMA: Failed to set the direction for pin %d\n", OUTPUT_PIN);
        return 1;
    }

    if (initPin(INTERRUPT_PIN)) { return 1; }
    if (gpio_direction_input(INTERRUPT_PIN) < 0)
    {
        printk(KERN_ERR "RTDMA: Failed to set the direction for pin %d\n", INTERRUPT_PIN);
        return 1;
    }

    interruptIRQ = gpio_to_irq(INTERRUPT_PIN);
    if (interruptIRQ < 0)
    {
        printk(KERN_ERR "RTDMA: Unable to request IRQ: %d\n", interruptIRQ);
        return 1;
    }

    ret = request_irq(interruptIRQ, gpio_isr, IRQF_TRIGGER_RISING , "RTDMA#interrupt", NULL);
    if (ret)
    {
        printk(KERN_ERR "RTDMA: Unable to request IRQ: %d\n", ret);
        if(interruptIRQ) { free_irq(interruptIRQ, NULL); }
        return 1;
    }

    printk("RTDMA: GPIO init finished\n");
    return 0;
}

void gpio_exit(void)
{
    printk("RTDMA: GPIO exit started\n");
    if (interruptIRQ) { free_irq(interruptIRQ, NULL); }
    gpio_free(INTERRUPT_PIN);
    gpio_free(OUTPUT_PIN);
    printk("RTDMA: GPIO exit finished\n");
}
