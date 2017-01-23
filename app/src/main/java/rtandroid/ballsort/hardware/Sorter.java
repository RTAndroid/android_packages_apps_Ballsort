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

package rtandroid.ballsort.hardware;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.util.Utils;
import rtandroid.root.PrivilegeElevator;

public class Sorter
{
    private static final String MODULE_NAME = Constants.MODULE_SORTING + ".ko";

    private static void extractModule(Context context)
    {
        boolean extracted = Utils.extractRawFile(context, R.raw.rtdma, Sorter.MODULE_NAME);
        if (!extracted) { throw new RuntimeException("Failed to extractModule the module!"); }
    }

    private static boolean isModuleLoaded() throws Exception
    {
        // list all available modules
        Process lsmod = Runtime.getRuntime().exec("lsmod");
        lsmod.waitFor();
        InputStream is = lsmod.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String loadedModules = s.hasNext() ? s.next() : "";
        is.close();

        // test if ours is in the list
        return loadedModules.contains(Constants.MODULE_SORTING);
    }

    private static void loadModule(Context context) throws Exception
    {
        if (isModuleLoaded()) { return; }

        String modulePath = context.getFilesDir().getAbsolutePath() + "/" + MODULE_NAME;
        Process insmod = Runtime.getRuntime().exec("insmod " + modulePath);
        insmod.waitFor();
        if (insmod.exitValue() != 0) { throw new RuntimeException("insmod module returned " + insmod.exitValue()); }
    }

    public static void prepare(Context context)
    {
        try
        {
            extractModule(context);

            Log.d(MainActivity.TAG, "Module load started");
            PrivilegeElevator.enableRoot();

            loadModule(context);
            if (!isModuleLoaded()) { throw new RuntimeException("Failed to insmod module!"); }

            System.loadLibrary("sorter");
            if (!openMemory()) { throw new RuntimeException("Failed to insmod module!"); }

            Log.d(MainActivity.TAG, "Module load finished");
            PrivilegeElevator.disableRoot();

        }
        catch (Error | Exception e)
        {
            Log.e(MainActivity.TAG, "Exception during sorting module loading");
            Log.e(MainActivity.TAG, e.getMessage());
        }
    }

    public static void unload()
    {
        try
        {
            if (!isModuleLoaded()) { return; }
            closeMemory();

            PrivilegeElevator.enableRoot();

            Process insmod = Runtime.getRuntime().exec("rmmod " + Constants.MODULE_SORTING);
            insmod.waitFor();
            if (insmod.exitValue() != 0) { Log.e(MainActivity.TAG, "rmmod returned " + insmod.exitValue()); }

            PrivilegeElevator.disableRoot();
            Log.i(MainActivity.TAG, "Sorting module unloaded");
        }
        catch (Error | Exception e)
        {
            Log.e(MainActivity.TAG, "Exception during sorting module unloading: " + e.getMessage());
        }
    }

    private static native boolean openMemory();
    private static native void closeMemory();
    public static native void setDelays(int baseDelay, int nextDelay);
    public static native int getBallCount();
    public static native void resetBallCount();
}
