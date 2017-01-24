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

package rtandroid.ballsort.hardware;

import rtandroid.ballsort.hardware.pins.TimedGPIOPin;

public class MockTimedPin extends TimedGPIOPin
{
    public boolean mWasOpened = false;
    public boolean mWasClosed = false;

    public MockTimedPin()
    {
        super("MockTimedPin", 0, false, false);
    }

    @Override
    public void setValue(boolean level)
    {
        if (level) { mWasOpened = true;}
              else { mWasClosed = true;}
    }
}
