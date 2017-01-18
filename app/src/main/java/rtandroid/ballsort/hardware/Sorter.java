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
import rtandroid.ballsort.util.Utils;
import rtandroid.root.PrivilegeElevator;

@SuppressLint({"SetWorldReadable", "SetWorldWritable"})
public class Sorter
{
    private static String sModulePath = "";
    public static boolean sModuleLoaded = false;

    public static void extract(Context context)
    {
        String moduleName = Constants.MODULE_SORTING + ".ko";
        sModulePath = context.getFilesDir().getAbsolutePath() + "/" + moduleName;

        boolean result = Utils.extractRawFile(context, R.raw.rtdma, moduleName);
        Log.i(MainActivity.TAG, "Sorting module extracted: " + result);
    }

    public static void load()
    {
        try
        {
            if (sModuleLoaded) { return; }

            // we will need root to open the memory
            PrivilegeElevator.enableRoot();

            // change permissions to 666
            boolean result = true;
            File module = new File(sModulePath);
            result = result && module.setReadable(true, false);
            result = result && module.setWritable(true, false);
            if (!result){ Log.e(MainActivity.TAG, "Failed setting module world-readable"); }

            Process insmod = Runtime.getRuntime().exec("insmod " + sModulePath);
            insmod.waitFor();
            if (insmod.exitValue() != 0){ Log.e(MainActivity.TAG, "insmod returned " + insmod.exitValue()); }

            // load the native library
            System.loadLibrary("sorter");

            // list all available modules
            Process lsmod = Runtime.getRuntime().exec("lsmod");
            lsmod.waitFor();
            InputStream is = lsmod.getInputStream();
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String loadedModules =  s.hasNext() ? s.next() : "";
            is.close();

            // test if ours was loaded
            Log.d(MainActivity.TAG, "List of all loaded modules: " + loadedModules);
            if (!loadedModules.contains("rtdma"))
            {
                Log.e(MainActivity.TAG, "Failed to load RTDMA module!");
                sModuleLoaded = false;
            }

            // now we can init
            sModuleLoaded = openMemory();
            Log.i(MainActivity.TAG, "Opening native memory returned '" + sModuleLoaded + "'");

            // everything finished
            PrivilegeElevator.disableRoot();
            Log.i(MainActivity.TAG, "Sorting module loaded");
        }
        catch (Error | Exception e) { Log.e(MainActivity.TAG, "Exception during sorting module loading: " + e.getMessage()); }
    }

    public static void unload()
    {
        try
        {
            if (!sModuleLoaded) { return; }
            closeMemory();

            PrivilegeElevator.enableRoot();

            Process insmod = Runtime.getRuntime().exec("rmmod " + sModulePath);
            insmod.waitFor();
            if (insmod.exitValue() != 0) { Log.e(MainActivity.TAG, "rmmod returned " + insmod.exitValue()); }
            sModuleLoaded = false;

            PrivilegeElevator.disableRoot();
            Log.i(MainActivity.TAG, "Sorting module unloaded");
        }
        catch (Error | Exception e) { Log.e(MainActivity.TAG, "Exception during sorting module unloading: " + e.getMessage()); }
    }

    private static native boolean openMemory();
    private static native void closeMemory();
    public static native void setDelays(int baseDelay, int nextDelay);
    public static native int getBallCount();
    public static native void resetBallCount();
}
