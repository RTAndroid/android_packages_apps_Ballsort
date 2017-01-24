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

package rtandroid.ballsort;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import rtandroid.ballsort.blocks.MockStateBlock;
import rtandroid.ballsort.util.Utils;

public class AStateBlockTest
{
    private MockStateBlock mStateBlock = null;

    @Before
    public void setupTest()
    {
        mStateBlock = new MockStateBlock();
    }

    @Test
    public void testStart()
    {
        // start thread and wait at least one loop pass
        mStateBlock.start();
        Utils.delayMs(10);

        // our handleState should set mIsRunning to false
        assertTrue(mStateBlock.isRunning());
        assertTrue(mStateBlock.mPrepareWasCalled);
        assertTrue(mStateBlock.mHandleStateWasCalled);

        // don't forget to terminate it again
        mStateBlock.terminate();
    }

    @Test
    public void testTerminate()
    {
        // start thread and wait at least one loop pass
        mStateBlock.start();
        Utils.delayMs(10);

        // now terminate it again and wait until it finishes
        mStateBlock.terminate();
        mStateBlock.waitFor();

        // our handleState should set mIsRunning to false
        assertFalse(mStateBlock.isRunning());
        assertTrue(mStateBlock.mCleanupWasCalled);
    }
}
