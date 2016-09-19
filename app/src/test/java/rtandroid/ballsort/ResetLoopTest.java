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

package rtandroid.ballsort;

import org.junit.Before;
import org.junit.Test;

import rtandroid.ballsort.blocks.MockSlingshot;
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.blocks.Slingshot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResetLoopTest extends ResetLoop
{
    @Before
    public void setupTest()
    {
        mSlingshot = new MockSlingshot();

    }

    @Test
    public void testSlingshotNotReady()
    {
        // set slingshot to something else
        MockSlingshot slingshot = (MockSlingshot) mSlingshot;
        slingshot.setNextState(Slingshot.SlingshotState.ROTATING);
        slingshot.mAllowShotCalled = false;

        // nothing should change
        handleState();
        assertFalse(slingshot.mAllowShotCalled);
    }

    @Test
    public void testCleanupMain()
    {
        cleanup();

        assertTrue(((MockSlingshot)mSlingshot).mWasTerminated);
    }
}
