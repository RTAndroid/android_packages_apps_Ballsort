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
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.util.Utils;

public class ResetService extends Service
{
    private ResetLoop mCurrentResetLoop;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(MainActivity.TAG, "Starting execution of the reset service");
        Utils.lockPower();

        mCurrentResetLoop = new ResetLoop();
        mCurrentResetLoop.start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        if (mCurrentResetLoop == null) { return; }
        Log.i(MainActivity.TAG, "Stopping execution of the reset service");

        mCurrentResetLoop.terminate();
        mCurrentResetLoop.waitFor();

        Utils.unlockPower();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
