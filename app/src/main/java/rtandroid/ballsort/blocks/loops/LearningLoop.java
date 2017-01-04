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

import rtandroid.ballsort.blocks.AStateBlock;
import rtandroid.ballsort.blocks.Feeder;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class LearningLoop extends AStateBlock
{
    protected enum MainStates
    {
        WAIT_FEEDER,
        RECIEVE_COLORINFO,
    }

    private MainStates mState = MainStates.WAIT_FEEDER;
    private Feeder mFeeder = null;

    public LearningLoop()
    {
        super("LearningLoop", Constants.THREAD_BLOCK_PRIORITY);

        mFeeder = new Feeder();
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
    }

    @Override
    protected void handleState()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();

        switch (mState)
        {

        // Wait for the next ball being prepared
        case WAIT_FEEDER:
            if (mFeeder.getFeederState() == Feeder.FeederState.READY) { mState = MainStates.RECIEVE_COLORINFO; }
            break;

        // Ask user about current Ball
        case RECIEVE_COLORINFO:

            //User has not yet chosen a color
            if(data.mLatestColor != null) { break; }

            mFeeder.allowDrop();
            Utils.delayMs(settings.AfterDropDelay);

            if (mFeeder.getFeederState() != Feeder.FeederState.DROPPING) { mState = MainStates.WAIT_FEEDER; }
            break;
        }
    }
}
