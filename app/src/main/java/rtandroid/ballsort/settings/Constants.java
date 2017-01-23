/*
 * Copyright (C) 2017 RTAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rtandroid.ballsort.settings;

import java.io.Serializable;

public class Constants implements Serializable
{
    public static final String MODULE_SORTING = "rtdma";
    public static final int THREAD_BLOCK_PRIORITY = 50;

                                                             // ---------------------------------------------------------|
                                                             // | conn |  pin  |  func       | export |  plan            |
                                                             // ---------------------------------------------------------|
    public static final int FEEDER_MOTOR_PIN_STEP = 24;      // |  10  |   26  |  INT16      |   24   |  pololu1_step    |
    public static final int FEEDER_MOTOR_PIN_ENABLE = 29;    // |  10  |   21  |  INT21      |   29   |  pololu1_enable  |
    public static final int FEEDER_MOTOR_PIN_REF = 34;       // |  11  |    5  |  INT26      |   34   |  ref_shalter1    |
    public static final int FEEDER_DROP_PIN = 28;            // |  10  |   20  |  INT20      |   28   |  ventil2         |

    public static final int PATTERN_IMAGE_BORDER = 6;
    public static final int PATTERN_IMAGE_SIZE = 80;
    public static final int PATTERN_COLUMN_COUNT = 6;
    public static final int PATTERN_COLUMN_CAPACITY = 8;
    public static final int PATTERN_VALVE_PIN = 18;          // |  10  |   15  |  INT10      |   18   |  ventil3         |

    public static final int I2C_SDA = 209;                   // |  10  |   16  |  I2C_1/SDA  |  209   |  farbsensor_sda  |
    public static final int I2C_SCL = 210;                   // |  10  |   14  |  I2C_1/SDL  |  210   |  farbsensor_scl  |

    public static final int SLINGSHOT_LIGHTSWITCH_PIN = 19;  // |  10  |   18  |  INT11      |   19   |  lichtschranke2  |
    public static final int SLINGSHOT_MOTOR_PIN_STEP = 30;   // |  10  |   19  |  INT22      |   30   |  pololu2_step    |
    public static final int SLINGSHOT_MOTOR_PIN_ENABLE = 22; // |  10  |   17  |  INT14      |   22   |  pololu2_enable  |
    public static final int SLINGSHOT_MOTOR_PIN_REF = 33;    // |  10  |   27  |  INT25      |   33   |  ref_schalter2   |
    public static final int SLINGSHOT_VALVE_PIN = 21;        // |  10  |   13  |  INT13      |   21   |  ventil4         |

    public static final int SORTER_LIGHTSWITCH_PIN = 31;     // |  10  |   22  |  INT23      |   31   |  lichtschranke1  |
    public static final int SORTER_VALVE_PIN = 25;           // |  10  |   24  |  INT17      |   25   |  ventil1         |
                                                             // ---------------------------------------------------------|
}
