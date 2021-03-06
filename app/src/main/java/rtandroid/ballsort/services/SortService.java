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

package rtandroid.ballsort.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.hardware.Sorter;
import rtandroid.ballsort.util.Utils;

public class SortService extends Service
{
    private SortLoop mCurrentSortLoop;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(MainActivity.TAG, "Starting execution of the sort service");
        Utils.lockPower();
        Sorter.prepare(this);

        mCurrentSortLoop = new SortLoop();
        mCurrentSortLoop.start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        if (mCurrentSortLoop == null) { return; }
        Log.i(MainActivity.TAG, "Stopping execution of the sort service");

        mCurrentSortLoop.terminate();
        mCurrentSortLoop.waitFor();

        Utils.unlockPower();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
