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

import rtandroid.ballsort.blocks.Feeder;
import rtandroid.ballsort.hardware.MockStepper;
import rtandroid.ballsort.hardware.MockTimedPin;
import rtandroid.ballsort.settings.SettingsManager;

import static org.junit.Assert.*;

public class FeederTest extends Feeder
{
    @Before
    public void setupTest()
    {
        mDropPin = new MockTimedPin();
        mStepper = new MockStepper();
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
    public void dropNextBallWhenReady()
    {
        mState = FeederState.READY;
        allowDrop();
        assertEquals(mState, FeederState.DROPPING);
    }

    @Test
    public void dropNextBallWhenNotReady()
    {
        mState = FeederState.ROTATING;
        allowDrop();
        assertEquals(mState, FeederState.ROTATING);
    }

    @Test
    public void stateReady()
    {
        mState = FeederState.READY;
        handleState();

        //No change should have occured
        assertEquals(mState, FeederState.READY);

        // External call when READY
        allowDrop();
        assertEquals(mState,FeederState.DROPPING);
    }

    @Test
    public void stateDroping()
    {
        MockTimedPin dropPin = (MockTimedPin) mDropPin;
        dropPin.mWasOpened = false;
        dropPin.mWasClosed = false;

        SettingsManager.getSettings().FeederValveOpenedDelay = 0;
        mState = FeederState.DROPPING;
        handleState();

        assertTrue(dropPin.mWasOpened);
        assertTrue(dropPin.mWasClosed);
        assertEquals(mState, FeederState.ROTATING);
    }
}
