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

import org.slf4j.helpers.Util;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorObject;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.blocks.color.classifier.IColorClassifier;
import rtandroid.ballsort.blocks.color.classifier.MeanColorClassifier;
import rtandroid.ballsort.blocks.color.classifier.NeuralColorClassifier;
import rtandroid.ballsort.blocks.color.classifier.TreeColorClassifier;
import rtandroid.ballsort.hardware.ColorSensor;
import rtandroid.ballsort.hardware.Sorter;
import rtandroid.ballsort.hardware.Stepper;
import rtandroid.ballsort.hardware.pins.TimedGPIOPin;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

/**
 * Rotating wheel block that drops the balls towards the valve
 */
public class Feeder extends AStateBlock
{
    public enum FeederState
    {
        ROTATING, // Next ball is moved to the sensor
        READY,    // Wait until we are allowed to drop
        DROPPING  // Ball is being droped
    }

    private static final int[] ROTATE_PATTERN = { Stepper.WHILE_OPENED, Stepper.WHILE_CLOSED, Stepper.WHILE_OPENED, 32 };
    private static final IColorClassifier[] COLOR_CLASSIFIER = { new TreeColorClassifier(), new MeanColorClassifier(), NeuralColorClassifier.getInstance() };

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

        if (!mColorSensor.open()) { Log.e(MainActivity.TAG, "Could not open the ColorSensor"); }
    }

    @Override
    protected void cleanup()
    {
        super.cleanup();

        if (!mColorSensor.close()) { Log.e(MainActivity.TAG, "Could not close the ColorSensor"); }

        mStepper.cleanup();
        mDropPin.cleanup();

        DataState data = SettingsManager.getData();
        data.FeederState = FeederState.READY.name();
    }

    @Override
    protected void cancel()
    {
        super.cancel();
        mStepper.cancel();
    }

    private ColorType detectColor()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();
        data.mDetectedColor = ColorType.EMPTY;

        int r = 0, g = 0, b = 0;
        for (int i = 0; i < settings.ColorSersorRepeats; i++)
        {
            int[] rgb = mColorSensor.receive();
            if (rgb == null) { continue; }

            r = (rgb[1] << 8) | rgb[0];
            g = (rgb[3] << 8) | rgb[2];
            b = (rgb[5] << 8) | rgb[4];

            Log.d(MainActivity.TAG, " - color sensor returned r=" + r + ", g=" + g + ", " + b + ")");
            Utils.delayMs(settings.ColorSensorDelay);
        }

        ColorType colorType;
        Log.d(MainActivity.TAG, "Detecting RGB values (" + r + ", " + g + ", " + b + ")");

        ColorObject color = new ColorObject(r, g, b);

        for (IColorClassifier classifier : COLOR_CLASSIFIER)
        {
            colorType = classifier.classify(color);
            Log.d(MainActivity.TAG, classifier.getName() + " -> " + colorType.name());
        }

        IColorClassifier used = COLOR_CLASSIFIER[settings.ColorDetection];
        colorType = used.classify(color);
        Log.d(MainActivity.TAG, "Using: "+ used.getName());

        data.mLatestColor = color;
        return colorType;
    }

    @Override
    protected void handleState()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();
        data.FeederState = mState.name();

        switch (mState)
        {
        // Rotate ball, that is currently in front of the color sensor to the drop
        case ROTATING:
            mStepper.doSteps(ROTATE_PATTERN);
            data.mDropColor = data.mQueuedColor;
            data.mQueuedColor = data.mDetectedColor;
            data.mDetectedColor = detectColor();
            Utils.delayMs(settings.BeforeDropDelay);
            mState = FeederState.READY;
            break;

        // Do nothing
        case READY:
            break;

        // Drop the ball
        case DROPPING:
            mDropPin.setValueForMs(settings.FeederValveOpenedDelay, settings.FeederAfterDropDelay);
            data.mDetectedBalls = Sorter.getBallCount();
            mState = FeederState.ROTATING;
            break;
        }
    }
}
