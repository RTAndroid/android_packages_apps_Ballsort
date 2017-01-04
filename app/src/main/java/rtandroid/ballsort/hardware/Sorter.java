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

package rtandroid.ballsort.hardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;
import rtandroid.root.PrivilegeElevator;

public class Sorter
{
    private static String sPath = null;
    private static boolean mLoaded = false;

    public static void extract(Context context)
    {
        sPath = context.getFilesDir().getAbsolutePath();

        String name = Constants.MODULE_SORTING + ".ko";
        boolean result = Utils.extractRawFile(context, R.raw.rtdma, name);
        Log.i(MainActivity.TAG, "Sorting module extracted: " + result);
    }

    @SuppressLint({"SetWorldReadable", "SetWorldWritable"})
    public static void load()
    {
        if (mLoaded) { return; }

        String moduleName = Constants.MODULE_SORTING + ".ko";
        String modulePath = sPath + "/" + moduleName;

        try
        {
            PrivilegeElevator.enableRoot();

            // change permissions to 666
            boolean result = true;
            File module = new File(modulePath);
            result = result && module.setReadable(true, false);
            result = result && module.setWritable(true, false);
            if (!result){ Log.e(MainActivity.TAG, "Failed setting module world-readable"); }

            Process insmod = Runtime.getRuntime().exec("insmod " + modulePath);
            insmod.waitFor();
            if (insmod.exitValue() != 0){ Log.e(MainActivity.TAG, "insmod returned " + insmod.exitValue()); }

            // load the native library
            System.loadLibrary("sorter");

            Process lsmod = Runtime.getRuntime().exec("lsmod");
            lsmod.waitFor();
            InputStream is = lsmod.getInputStream();
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String loadedModules =  s.hasNext() ? s.next() : "";
            is.close();
            Log.d(MainActivity.TAG, "Loaded Modules: "+loadedModules);

            if(!loadedModules.contains("rtdma"))
            {
                Log.e(MainActivity.TAG, "RTDMA did not load");
                DataState data = SettingsManager.getData();
                data.mModuleError = "MODULE NOT LOADED!";
            }

            // now we can init
            result = openMemory();
            Log.i(MainActivity.TAG, "Opening native memory returned '" + result + "'");

            PrivilegeElevator.disableRoot();
            mLoaded = result;
        }
        catch (Error | Exception e) { Log.e(MainActivity.TAG, "Exception during sorting module loading: " + e.getMessage()); }

        Log.i(MainActivity.TAG, "Sorting module loaded");
    }

    public static void unload()
    {
        if(!mLoaded) { return; }

        closeMemory();

        String moduleName = Constants.MODULE_SORTING + ".ko";
        String modulePath = sPath + "/" + moduleName;

        try
        {
            PrivilegeElevator.enableRoot();

            Process insmod = Runtime.getRuntime().exec("rmmod " + modulePath);
            insmod.waitFor();
            if (insmod.exitValue() != 0){ Log.e(MainActivity.TAG, "rmmod returned " + insmod.exitValue()); }

            PrivilegeElevator.disableRoot();
        }
        catch (Error | Exception e) { Log.e(MainActivity.TAG, "Exception during sorting module unloading: " + e.getMessage()); }

        Log.i(MainActivity.TAG, "Sorting module unloaded");
        mLoaded = false;
    }

    /** Sends the important delayMs information to the kernel module and receives the current number of balls */
    private static native boolean openMemory();
    public static native void setDelays(int baseDelay, int nextDelay);
    public static native int getBallCount();
    private static native void closeMemory();

}
