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
    private int mBallsDropped;
    private int mNextColumn;

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
        mBallsDropped = Sorter.getBallCount();

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
            int count = Sorter.getBallCount();
            if (mBallsDropped != count)
            {
                mBallsDropped = count;
                mColorPattern.onBallDropped(mNextColumn);
            }
            if (mColorPattern.isFull()) { terminate(); } // no need to process if everything is finished
                                   else { mState = MainStates.WAIT_FEEDER; }
            break;

        // Wait for the next ball being prepared
        case WAIT_FEEDER:
            if (mFeeder.getFeederState() == Feeder.FeederState.READY) { mState = MainStates.RECIEVE_COLORINFO; }
            break;

        // Measure current ball
        case RECIEVE_COLORINFO:
            mNextColumn = mColorPattern.getNextColumn(data.mDropColor);
            int delayUs = (mNextColumn == ColorPattern.SKIP) ? 0 : settings.ColumnDelaysUs[mNextColumn];
            Log.d(MainActivity.TAG, "Color: " + data.mDropColor.name() + " will be shot into column " + mNextColumn);
            Sorter.setDelays(settings.BaseDelayMs, delayUs);

            mState = MainStates.DROP_BALL;
            break;

        // Drop ball
        case DROP_BALL:
            mColorPattern.preparePins();
            Utils.delayMs(settings.BeforeDropDelay);
            mFeeder.allowDrop();
            Utils.delayMs(settings.AfterDropDelay);
            if (mFeeder.getFeederState() != Feeder.FeederState.DROPPING) { mState = MainStates.WAIT_PATTERN; }
            break;
        }
    }
}
