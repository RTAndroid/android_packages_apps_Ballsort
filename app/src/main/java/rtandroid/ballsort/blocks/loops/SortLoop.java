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

package rtandroid.ballsort.blocks.loops;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.AStateBlock;
import rtandroid.ballsort.blocks.color.ColorPattern;
import rtandroid.ballsort.blocks.Feeder;
import rtandroid.ballsort.hardware.Sorter;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class SortLoop extends AStateBlock
{
    protected enum MainStates
    {
        WAIT_PATTERN,
        WAIT_FEEDER,
        RECIEVE_COLORINFO,
        DROP_BALL,
    }

    protected MainStates mState = MainStates.WAIT_FEEDER;
    protected Feeder mFeeder = null;
    protected ColorPattern mColorPattern = null;

    public SortLoop()
    {
        super("SortLoop", Constants.THREAD_BLOCK_PRIORITY);

        mFeeder = new Feeder();
        mColorPattern = new ColorPattern();
        mState = MainStates.WAIT_FEEDER;
    }

    @Override
    public void prepare()
    {
        super.prepare();

        // start all the other threads
        mFeeder.start();
    }

    @Override
    public void cleanup()
    {
        super.cleanup();

        // terminate all the other threads
        mFeeder.terminate();

        // now wait for all of them to terminate
        mFeeder.waitFor();

        mColorPattern.cleanup();
    }

    @Override
    protected void handleState()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();

        switch (mState)
        {
            // Wait for the ball to arrive
            case WAIT_PATTERN:
                // no need to process if everything is finished
                if (mColorPattern.isFull())
                {
                    terminate();
                }
                else { mState = MainStates.WAIT_FEEDER; }
                break;

            // Wait for the next to-drop ball
            case WAIT_FEEDER:
                if (mFeeder.getFeederState() == Feeder.FeederState.READY) { mState = MainStates.RECIEVE_COLORINFO; }
                break;

            // Measure current ball
            case RECIEVE_COLORINFO:


                int nextColumn = mColorPattern.getNextColumn(data.mDropColor);
                int delayUs = (nextColumn == ColorPattern.SKIP) ? 0 : settings.ColumnDelaysUs[nextColumn];

                Log.d(MainActivity.TAG, "Next color: " + data.mDropColor.name());
                Log.d(MainActivity.TAG, "Next column: " + nextColumn);
                Log.d(MainActivity.TAG, "Sorter delayMs: " + delayUs);

                Sorter.setDelays(settings.BaseDelayMs, delayUs);
                Utils.delayMs(settings.BeforeDropDelay);
                mState = MainStates.DROP_BALL;
                break;

            // Drop ball
            case DROP_BALL:
                mColorPattern.preparePins();
                mFeeder.allowDrop();
                Utils.delayMs(settings.NextBallDelay);
                if(mFeeder.getFeederState() != Feeder.FeederState.DROPPING) { mState = MainStates.WAIT_PATTERN; }
                break;
        }
    }
}
