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
import rtandroid.ballsort.blocks.SlingshotValve;
import rtandroid.ballsort.blocks.SlingshotMotor;
import rtandroid.ballsort.blocks.color.ColorPattern;
import rtandroid.ballsort.settings.Constants;

public class ResetLoop extends AStateBlock
{
    protected SlingshotValve mSlingshotValve = null;
    protected SlingshotMotor mSlingshotMotor = null;
    protected ColorPattern mColorRows = null;

    public ResetLoop()
    {
        super("ResetLoop", Constants.THREAD_BLOCK_PRIORITY);

        mSlingshotValve = new SlingshotValve();
        mSlingshotMotor = new SlingshotMotor();
        mColorRows = new ColorPattern();
    }

    @Override
    public void prepare()
    {
        super.prepare();

        // start all the other threads
        mSlingshotValve.start();
        mSlingshotMotor.start();

        // Open the pattern
        mColorRows.resetPattern();
    }

    @Override
    public void cleanup()
    {
        super.cleanup();

        // terminate all the other threads
        mSlingshotValve.terminate();
        mSlingshotMotor.terminate();

        // now wait for all of them to terminate
        mSlingshotValve.waitFor();
        mSlingshotMotor.waitFor();

        mColorRows.cleanup();
    }

    @Override
    protected void handleState()
    {
        if (mSlingshotValve.isError()) { mSlingshotMotor.forbid(); }
                                        else { mSlingshotMotor.allow(); }

        // there is currently nothing to do here
        if (!mSlingshotValve.ballReady()) { return; }

        // we know there is a ball ready to be shot
        mSlingshotValve.allowShot();
    }
}
