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

package rtandroid.ballsort.hardware.pins;

import rtandroid.ballsort.util.Utils;

/**
 * This class extens the default GPIOPin for timed IO
 */
public class TimedGPIOPin extends GPIOPin
{
    private boolean mInverted = false;

    public TimedGPIOPin(String name, int id, boolean inverted, boolean debug)
    {
        super(name, id, GPIOPin.DIRECTION_OUT, false);
        mInverted = inverted;
    }

    public void setValueForUs(int bothDelay)
    {
        setValueForUs(bothDelay, bothDelay);
    }

    private void setValueForUs(int firstDelay, int secondDelay)
    {
        setValue(!mInverted);
        Utils.delayUs(firstDelay);

        setValue(mInverted);
        Utils.delayUs(secondDelay);
    }

    public void setValueForMs(int bothDelay)
    {
       setValueForMs(bothDelay, bothDelay);
    }
    
    public void setValueForMs(int firstDelay, int secondDelay)
    {
        setValue(!mInverted);
        Utils.delayMs(firstDelay);

        setValue(mInverted);
        Utils.delayMs(secondDelay);
    }
}
