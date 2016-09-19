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

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.space.ColorRGB;
import rtandroid.ballsort.blocks.color.classifeir.MeanColorClassifier;
import rtandroid.ballsort.blocks.color.classifeir.IColorClassifier;
import rtandroid.ballsort.settings.Constants;
import rtandroid.root.PrivilegeElevator;

public class ColorSensor
{
    private static final IColorClassifier COLOR_CLASSIFIER = new MeanColorClassifier();

    public boolean open()
    {
        try
        {
            PrivilegeElevator.enableRoot();
            Process modprobe = Runtime.getRuntime().exec("modprobe i2c-gpio-custom bus0=10," + Constants.I2C_SDA + "," + Constants.I2C_SCL);
            modprobe.waitFor();

            if (modprobe.exitValue() != 0) {
                Log.e(MainActivity.TAG, "Failed to modprobe i2c-gpio-custom");
                return false;
            }
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, e.getMessage());
            return false;
        }

        try { System.loadLibrary("color"); }
        catch (Error | Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to open I2C bus: " + e.getMessage());
            return false;
        }

        // we have to init as root
        boolean result = openI2C();
        Log.i(MainActivity.TAG, "Opening i2c color sensor returned '" + result + "'");

        try
        {
            PrivilegeElevator.disableRoot();
        }
        catch(Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to close root: " + e.getMessage());
            return false;
        }

        Log.d(MainActivity.TAG, "I2C pin was initialized");
        return true;
    }

    public ColorData detectColor()
    {
        long values = readI2C();

        int r = (int)(values >> 32) & 0xFFFF;
        int g = (int)(values >> 16) & 0xFFFF;
        int b = (int)(values) & 0xFFFF;
        ColorRGB rgb = new ColorRGB(r, g, b);

        Log.d(MainActivity.TAG, "Detecting the color using "+ COLOR_CLASSIFIER.getName() + " classifier...");
        return COLOR_CLASSIFIER.classify(rgb);
    }

    public boolean close()
    {
        if (!closeI2C())
        {
            Log.e(MainActivity.TAG, "Failed to close the I2C pin");
            return false;
        }

        return true;
    }

    /**
     * Native Linux interface for ioctl and lowlevel I/O
     */

    private static native boolean openI2C();
    private static native long readI2C();
    private static native boolean closeI2C();
}
