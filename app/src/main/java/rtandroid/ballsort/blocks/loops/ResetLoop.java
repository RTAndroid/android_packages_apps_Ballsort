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
import rtandroid.ballsort.blocks.SlingshotLightSwitch;
import rtandroid.ballsort.blocks.SlingshotMotor;
import rtandroid.ballsort.blocks.color.ColorPattern;
import rtandroid.ballsort.settings.Constants;

public class ResetLoop extends AStateBlock
{
    protected SlingshotLightSwitch mSlingshotLightSwitch = null;
    protected SlingshotMotor mSlingshotMotor = null;
    protected ColorPattern mColorRows = null;

    public ResetLoop()
    {
        super("ResetLoop", Constants.THREAD_BLOCK_PRIORITY);

        mSlingshotLightSwitch = new SlingshotLightSwitch();
        mSlingshotMotor = new SlingshotMotor();
        mColorRows = new ColorPattern();
    }

    @Override
    public void prepare()
    {
        super.prepare();

        // start all the other threads
        mSlingshotLightSwitch.start();
        mSlingshotMotor.start();

        // Open the pattern
        mColorRows.resetPattern();
    }

    @Override
    public void cleanup()
    {
        super.cleanup();

        // terminate all the other threads
        mSlingshotLightSwitch.terminate();
        mSlingshotMotor.terminate();

        // now wait for all of them to terminate
        mSlingshotLightSwitch.waitFor();
        mSlingshotMotor.waitFor();

        mColorRows.cleanup();
    }

    @Override
    protected void handleState()
    {
        if (mSlingshotLightSwitch.isError()) { mSlingshotMotor.forbid(); }
        else { mSlingshotMotor.allow(); }

        if (mSlingshotLightSwitch.ballReady()) { mSlingshotLightSwitch.allowShot(); }

    }
}
