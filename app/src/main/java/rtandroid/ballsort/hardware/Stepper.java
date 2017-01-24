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

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.hardware.pins.GPIOPin;
import rtandroid.ballsort.hardware.pins.TimedGPIOPin;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class Stepper
{
    public static final int WHILE_CLOSED = -1;
    public static final int WHILE_OPENED = -2;

    private String mName = "Unknown";
    private int mStepDelay = 0;
    private boolean mIsBusy = false;

    protected GPIOPin mEnablePin = null;
    protected TimedGPIOPin mStepPin = null;
    private GPIOPin mRefPin = null;
    private boolean mEmergencyWait = false;

    public Stepper(String name, int enablePin, int stepPin, int stepDelay, int referencePin)
    {
        mName = name;
        mStepDelay = stepDelay;

        mEnablePin = new GPIOPin(mName + "Enable", enablePin, GPIOPin.DIRECTION_OUT, false);
        mStepPin = new TimedGPIOPin(mName + "Step", stepPin, false, false);
        mRefPin = new GPIOPin(mName + "Ref", referencePin, GPIOPin.DIRECTION_IN, false);
    }

    public boolean isEmergencyWaiting()
    {
        return mEmergencyWait;
    }

    public void emergencyWait(boolean emergency)
    {
        mEmergencyWait = emergency;
    }

    /**
     * Activates motor power.
     */
    protected void enable()
    {
        mEnablePin.setValue(true);

        Settings settings = SettingsManager.getSettings();
        Utils.delayMs(settings.StepperEnableDelay);
    }

    /**
     * Cancels the current stepping operation.
     */
    public void cancel()
    {
        mIsBusy = false;
    }

    /**
     * Deactivates motor power.
     */
    protected void disable()
    {
        mEnablePin.setValue(false);

        Settings settings = SettingsManager.getSettings();
        Utils.delayMs(settings.StepperDisableDelay);
    }

    /**
     * Lets the motor do some steps
     * @param steps array of numbers of steps
     */
    public void doSteps(int[] steps)
    {
        if (mIsBusy)
        {
            Log.e(MainActivity.TAG, "Ignoring steps for stepper " + mName + ", as it is already moving");
            return;
        }

        mIsBusy = true;
        enable();

        for (int step : steps) { doStep(step); }

        disable();
        mIsBusy = false;
    }

    private void doStep(int step)
    {
        // don't allow unknown negative constants
        if (step < WHILE_OPENED)
        {
            Log.e(MainActivity.TAG, "Ignoring illegal step type: " + step);
            return;
        }

        switch (step)
        {
        case WHILE_CLOSED:
            while (mIsBusy && mRefPin.getValue()) { doSafeStep(); }
            break;

        case WHILE_OPENED:
            while (mIsBusy && !mRefPin.getValue())  { doSafeStep(); }
            break;

        default:
            while (mIsBusy && step > 0) { step -= doSafeStep(); }
            break;
        }
    }

    /**
     *   Only returns a step if mEmergencyWait was not set
     */
    private int doSafeStep()
    {
        if(mEmergencyWait)
        {
            Settings settings = SettingsManager.getSettings();
            Utils.delayMs(settings.BusyWaitDelay);
            return 0;
        }

        mStepPin.setValueForUs(mStepDelay);

        return 1;
    }

    public void cleanup()
    {
        mEnablePin.cleanup();
        mRefPin.cleanup();
        mStepPin.cleanup();
    }
}
