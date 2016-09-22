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

package rtandroid.ballsort.blocks;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.hardware.pins.GPIOPin;
import rtandroid.ballsort.hardware.pins.TimedGPIOPin;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

/**
 * Block that shoots the balls back into the feeder. Used for resetting.
 */
public class SlingshotValve extends AStateBlock
{
    private enum SlingshotState
    {
        CHECKING, // Check if the ball is in the lightswitch
        READY,    // Ball is ready to be shot
        SHOOTING, // Ball is being shot
    }

    private long mLastTimestamp = 0;
    private boolean mError = false;

    protected SlingshotState mState = SlingshotState.CHECKING;
    protected GPIOPin mLightswitch = null;
    protected TimedGPIOPin mValvePin = null;
    protected int mEmptyRotations = 0;

    public SlingshotValve()
    {
        super("SlingshotValve", Constants.THREAD_BLOCK_PRIORITY);

        int lightSwitchPinID = Constants.SLINGSHOT_LIGHTSWITCH_PIN;
        mLightswitch = new GPIOPin("SlingshotLightswitch", lightSwitchPinID, GPIOPin.DIRECTION_IN, false);

        int valvePinID = Constants.SLINGSHOT_VALVE_PIN;
        mValvePin = new TimedGPIOPin("SlingshotValve", valvePinID, false, false);
    }

    public void allowShot()
    {
        mState = SlingshotState.SHOOTING;
    }

    public boolean ballReady()
    {
        return mState == SlingshotState.READY;
    }

    public boolean isError()
    {
        return mError;
    }

    @Override
    protected void cleanup()
    {
        mLightswitch.cleanup();
        mValvePin.cleanup();

        DataState data = SettingsManager.getData();
        data.SlingshotValveState = SlingshotState.READY.name();
    }

    @Override
    protected void handleState()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();
        data.SlingshotValveState = mState.name();

        switch (mState)
        {
        // Shoot ball up
        case SHOOTING:
            Utils.delayMs(settings.SlingshotDelayBeforeShoot);
            mValvePin.setValueForMs(settings.SlingshotValveOpenDelay);
            mState = SlingshotState.CHECKING;
            break;

        // Wait for all to arrive
        case CHECKING:
            long timestampUs = System.nanoTime() / 1000;
            long delta = timestampUs - mLastTimestamp;

            boolean ready = !mLightswitch.getValue();
            if (ready && delta < settings.SlingshotErrorThreshold) { mError = true; }
            if (mError && delta > settings.SlingshotFreeThreshold) { mError = false; }

            if (ready)
            {
                Log.d(MainActivity.TAG, "");
                mState = SlingshotState.READY;
                mEmptyRotations = 0;
                mLastTimestamp = timestampUs;
            }
            break;

        // Do nothing until a ball is in front of the slighshot
        case READY:
            if (mLightswitch.getValue()) { mState = SlingshotState.CHECKING; }
            break;
        }
    }
}
