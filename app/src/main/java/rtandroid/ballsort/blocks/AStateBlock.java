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

package rtandroid.ballsort.blocks;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

/**
 * Represents a hardware feature block
 */
public abstract class AStateBlock extends Thread
{
    private int mThreadPriority = 0;
    private boolean mIsRunning = false;
    private String mName = "AStateBlock";

    protected AStateBlock(String name, int prio)
    {
        mName = name;
        mThreadPriority = prio;
    }

    /**
     * Returns the current state of the thread
     * @return if the thread is running
     */
    public boolean isRunning()
    {
        return mIsRunning;
    }

    /**
     * prepares the thread logging tag and RT priority
     */
    protected void prepare()
    {
        setName("Ballsort_" + mName);
        Utils.setThreadPriority(mThreadPriority);
    }

    /**
     * Statemachine main loop
     */
    @Override
    public void run()
    {
        Log.i(MainActivity.TAG, mName + " thread started");
        prepare();

        mIsRunning = true;
        int delay = SettingsManager.getSettings().BusyWaitDelay;

        Log.i(MainActivity.TAG, mName + " loop started");
        while (mIsRunning)
        {
            Utils.delayMs(delay);
            handleState();
        }
        Log.i(MainActivity.TAG, mName + " loop terminated");

        cleanup();
        Log.i(MainActivity.TAG, mName + " thread terminated");
    }

    protected void cancel()
    {
        // nothing to do
    }

    protected void cleanup()
    {
        // nothing to do
    }

    public void terminate()
    {
        Log.i(MainActivity.TAG, "Stopping block " + mName + "...");

        mIsRunning = false;
        cancel();
    }

    public void waitFor()
    {
        try { join(); }
        catch (Exception ignored) { }
    }

    /**
     * Will wrap around the different state enums so we can call the state code from within tests.
     */
    protected abstract void handleState();
}
