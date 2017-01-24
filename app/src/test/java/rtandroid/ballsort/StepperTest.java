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

package rtandroid.ballsort;

import org.junit.Before;
import org.junit.Test;

import rtandroid.ballsort.hardware.MockOutputPin;
import rtandroid.ballsort.hardware.MockStepper;
import rtandroid.ballsort.hardware.MockTimedPin;
import rtandroid.ballsort.hardware.Stepper;
import rtandroid.ballsort.settings.SettingsManager;

import static org.junit.Assert.*;

public class StepperTest extends Stepper
{
    private MockStepper mStepper = null;

    public StepperTest()
    {
        super("StepperTest", 0, 0, 0, 0);
    }

    @Before
    public void setupTest()
    {
        mStepper = new MockStepper();
        mEnablePin = new MockOutputPin();
        mStepPin = new MockTimedPin();
    }

    @Test
    public void testEnable()
    {
        MockOutputPin enablePin = (MockOutputPin) mEnablePin;
        enablePin.mWasOpened = false;
        enablePin.mWasClosed = false;

        SettingsManager.getSettings().StepperEnableDelay = 0;
        enable();

        assertTrue(enablePin.mWasOpened);
        assertFalse(enablePin.mWasClosed);
    }

    @Test
    public void testSteps()
    {
        mStepper.mDidSteps = false;
        mStepper.doSteps(new int[]{});
        assertTrue(mStepper.mDidSteps);
    }

    @Test
    public void testDisable()
    {
        MockOutputPin enablePin = (MockOutputPin) mEnablePin;
        enablePin.mWasOpened = false;
        enablePin.mWasClosed = false;

        SettingsManager.getSettings().StepperDisableDelay = 0;
        disable();

        assertFalse(enablePin.mWasOpened);
        assertTrue(enablePin.mWasClosed);
    }
}
