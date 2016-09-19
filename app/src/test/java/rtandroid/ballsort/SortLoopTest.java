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

import rtandroid.ballsort.blocks.Feeder;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.blocks.MockColorPattern;
import rtandroid.ballsort.blocks.MockFeeder;

import static org.junit.Assert.*;

public class SortLoopTest extends SortLoop
{
    @Before
    public void setupTest()
    {
        mColorPattern = new MockColorPattern();
        mFeeder = new MockFeeder();
    }

    @Test
    public void testInitialState()
    {
        assertEquals(mState, MainStates.WAIT_FEEDER);
    }

    @Test
    public void testFeederReady()
    {
        MockFeeder feeder = (MockFeeder) mFeeder;
        feeder.setState(Feeder.FeederState.READY);

        // feeder is ready, we should change the state
        mState = MainStates.WAIT_FEEDER;
        handleState();
        assertEquals(mState, MainStates.RECIEVE_COLORINFO);
    }

    @Test
    public void testCleanupMain()
    {
        cleanup();

        assertTrue(((MockColorPattern) mColorPattern).mWasTerminated);
        assertTrue(((MockFeeder)mFeeder).mWasTerminated);
    }
}
