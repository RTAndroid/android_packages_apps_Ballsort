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

package rtandroid.ballsort.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rtandroid.ballsort.MainActivity;
import rtandroid.realtime.RealTimeProxy;

public class Utils
{
    static { System.loadLibrary("util"); }

    private static native void nativeSleepUs(int usec);

    public static boolean extractRawFile(Context context, int resourceID, String targetFileName)
    {
        try
        {
            InputStream is = context.getResources().openRawResource(resourceID);

            File target = new File(context.getFilesDir(), targetFileName);
            OutputStream outputStream = new FileOutputStream(target);

            byte buffer[] = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) { outputStream.write(buffer, 0, length); }

            outputStream.close();
            is.close();

            return true;
        }
        catch (IOException e)
        {
            Log.e(MainActivity.TAG, "Failed to extract the asset: " + e.getMessage());
            return false;
        }
    }

    public static void delayMs(int msec)
    {
        if (msec == 0) { return; }

        try { Thread.sleep(msec); }
        catch (InterruptedException ignored) { /* we can ignore this one */ }
    }

    public static void delayUs(int usec)
    {
        nativeSleepUs(usec);
    }

    public static void lockPower()
    {
        try
        {
            RealTimeProxy proxy = new RealTimeProxy();
            proxy.lockCpuPower(100);
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to lock power: " + e.getMessage());
        }
    }

    public static void unlockPower()
    {
        try
        {
            RealTimeProxy proxy = new RealTimeProxy();
            proxy.unlockCpuPower();
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to unlock power: " + e.getMessage());
        }
    }

    public static void setThreadPriority(int prio)
    {
        try
        {
            RealTimeProxy proxy = new RealTimeProxy();
            proxy.setSchedulingPolicy(rtandroid.Constants.SCHED_POLICY_FIFO);
            proxy.setPriority(prio);
            proxy.setAffinity(2);
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to set RT priority: " + e.getMessage());
        }
    }
}
