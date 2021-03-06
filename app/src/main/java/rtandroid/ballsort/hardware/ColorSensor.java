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

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorRGB;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.util.RootUtils;

public class ColorSensor
{
    public boolean open()
    {
        RootUtils.enableRoot();

        try
        {
            Process modprobe = Runtime.getRuntime().exec("modprobe i2c-gpio-custom bus0=10," + Constants.I2C_SDA + "," + Constants.I2C_SCL);
            modprobe.waitFor();
            if (modprobe.exitValue() != 0) { throw new RuntimeException("Failed to modprobe i2c-gpio-custom"); }
        }
        catch (Error | Exception e)
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

        boolean result = openI2C();
        Log.i(MainActivity.TAG, "Opening i2c color sensor returned '" + result + "'");

        RootUtils.disableRoot();
        return true;
    }

    public ColorRGB receive()
    {
        ColorRGB color = new ColorRGB();

        int[] rgb = readSensor();
        if (rgb == null) { return color; }

        color.r = (rgb[1] << 8) | rgb[0];
        color.g = (rgb[3] << 8) | rgb[2];
        color.b = (rgb[5] << 8) | rgb[4];

        return color;
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
     * Native Linux interface for ioctl and low-level I/O
     */

    private static native boolean openI2C();
    private static native int[] readSensor();
    private static native boolean closeI2C();
}
