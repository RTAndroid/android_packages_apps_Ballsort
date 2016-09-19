/*
 * Copyright (C) 2016 RTAndroid Project
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

import rtandroid.ballsort.blocks.Slingshot;
import rtandroid.ballsort.hardware.MockInputPin;
import rtandroid.ballsort.hardware.MockStepper;
import rtandroid.ballsort.hardware.MockTimedPin;
import rtandroid.ballsort.settings.SettingsManager;

import static org.junit.Assert.*;

public class SlingshotTest extends Slingshot
{
    @Before
    public void setupTest()
    {
        mStepper = new MockStepper();
        mValvePin = new MockTimedPin();
        mLightswitch = new MockInputPin();
    }

    @Test
    public void testInitialState()
    {
        assertEquals(mState, SlingshotState.CHECKING);
    }

    @Test
    public void testCancel()
    {
        MockStepper stepper = (MockStepper) mStepper;
        stepper.mWasCancelled = false;

        // this should cancel the stepper
        cancel();

        assertTrue(stepper.mWasCancelled);
    }

    @Test
    public void testReadyStateWhenReady()
    {
        MockInputPin lightswitch = (MockInputPin) mLightswitch;
        lightswitch.mCurrentValue = false;
        lightswitch.mGotValue = false;

        mState = SlingshotState.READY;
        handleState();

        assertEquals(mState, SlingshotState.READY);
        assertTrue(lightswitch.mGotValue);
    }

    @Test
    public void testReadyStateWhenNotReady()
    {
        MockInputPin lightswitch = (MockInputPin) mLightswitch;
        lightswitch.mCurrentValue = true;
        lightswitch.mGotValue = false;

        mState = SlingshotState.READY;
        handleState();

        assertEquals(mState, SlingshotState.ROTATING);
        assertTrue(lightswitch.mGotValue);
    }

    @Test
    public void testAllowShot()
    {
        mState = SlingshotState.READY;
        allowShot();
        assertEquals(mState, SlingshotState.SHOOTING);
    }

    @Test
    public void testValveTriggerOnShoot()
    {
        MockTimedPin valve = (MockTimedPin) mValvePin;
        valve.mWasOpened = false;
        valve.mWasClosed = false;

        SettingsManager.getSettings().SlingshotValveOpenDelay = 0;
        SettingsManager.getSettings().SlingshotDelayAfterShoot = 0;
        mState = SlingshotState.SHOOTING;
        handleState();

        assertTrue(valve.mWasOpened);
        assertTrue(valve.mWasClosed);
    }

    @Test
    public void testStateAfterShooting()
    {
        SettingsManager.getSettings().SlingshotValveOpenDelay = 0;
        SettingsManager.getSettings().SlingshotDelayAfterShoot = 0;
        mState = SlingshotState.SHOOTING;
        handleState();

        assertEquals(mState, SlingshotState.CHECKING);
    }

    @Test
    public void testStateAfterRotating()
    {
        SettingsManager.getSettings().SlingshotDelayAfterRotate = 0;
        mState = SlingshotState.ROTATING;
        handleState();

        assertEquals(mState, SlingshotState.CHECKING);
    }

    @Test
    public void testMotorTriggerOnRotating()
    {
        MockStepper stepper = (MockStepper) mStepper;
        stepper.mDidSteps = false;

        SettingsManager.getSettings().SlingshotDelayAfterRotate = 0;
        mState = SlingshotState.ROTATING;
        handleState();

        assertTrue(stepper.mDidSteps);
    }
}
