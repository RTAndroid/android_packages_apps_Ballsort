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
import rtandroid.ballsort.hardware.ColorSensor;
import rtandroid.ballsort.hardware.Stepper;
import rtandroid.ballsort.hardware.pins.TimedGPIOPin;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

/**
 * Rotating wheel block that drops the balls towards the valve
 */
public class Feeder extends AStateBlock
{
    public enum FeederState
    {
        ROTATING, // Next ball is moved to the sensor
        READY,    // Wait until we are allowed to drop
        DROPPING   // Ball is beeing droped
    }

    private static final int[] ROTATE_PATTERN = { Stepper.WHILE_OPENED, Stepper.WHILE_CLOSED, Stepper.WHILE_OPENED, 32 };

    protected FeederState mState = FeederState.DROPPING;
    protected Stepper mStepper = null;
    protected TimedGPIOPin mDropPin = null;

    private ColorSensor mColorSensor = null;

    public Feeder()
    {
        super("Feeder", Constants.THREAD_BLOCK_PRIORITY);

        mColorSensor = new ColorSensor();

        int stepperEnablePinID = Constants.FEEDER_MOTOR_PIN_ENABLE;
        int stepperStepPinID = Constants.FEEDER_MOTOR_PIN_STEP;
        int stepperDelay = SettingsManager.getSettings().FeederStepperPwmDelay;
        mStepper = new Stepper("Feeder", stepperEnablePinID, stepperStepPinID, stepperDelay, Constants.FEEDER_MOTOR_PIN_REF);

        int dropPinID = Constants.FEEDER_DROP_PIN;
        mDropPin = new TimedGPIOPin("Feeder", dropPinID, true, false);
    }

    public FeederState getFeederState()
    {
        return mState;
    }

    public void allowDrop()
    {
        if (getFeederState() != FeederState.READY) { return; }
        if (getFeederState() == FeederState.DROPPING) { return; }

        Log.d(MainActivity.TAG, "Allowing feeder drop...");
        mState = FeederState.DROPPING;
    }

    @Override
    protected void prepare()
    {
        super.prepare();
        if(!mColorSensor.open())
        {
            Log.e(MainActivity.TAG, "Could not open the ColorSensor");
        }
    }

    @Override
    protected void cleanup()
    {
        super.cleanup();
        if(!mColorSensor.close())
        {
            Log.e(MainActivity.TAG, "Could not close the ColorSensor");
        }
        mStepper.cleanup();
        mDropPin.cleanup();

        DataState data = SettingsManager.getData();
        data.FeederState = Constants.BLOCK_STOPPED;
    }

    @Override
    protected void cancel()
    {
        super.cancel();
        mStepper.cancel();
    }

    @Override
    public void handleState()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();
        data.FeederState = mState.name();

        switch (mState)
        {
            // Rotate ball, that is currently in front of the color sensor to the drop
            case ROTATING:
                mStepper.doSteps(ROTATE_PATTERN);
                data.mDropColor = data.mNextColor;
                data.mNextColor = data.mQueuedColor;
                data.mQueuedColor = mColorSensor.detectColor();
                mState = FeederState.READY;
                break;
            // Do nothing
            case READY:
                break;
            // Drop the ball
            case DROPPING:
                mDropPin.setValueForMs(settings.FeederValveDropDelay, 0);
                data.BallsInFeeder--;
                data.BallsInPattern++;
                mState = FeederState.ROTATING;
                break;
        }
    }
}
