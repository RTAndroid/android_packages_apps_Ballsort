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

package rtandroid.ballsort.hardware.pins;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.util.RootUtils;

/**
 * Basic class for I/O
 */
public class GPIOPin
{
    public static final String DIRECTION_IN = "in";
    public static final String DIRECTION_OUT = "out";

    private final String mName;
    private final Integer mPinID;
    private final String mDirection;
    private final boolean mDebug;

    private String mFilename = null;
    private RandomAccessFile mInput = null;
    private FileOutputStream mOutput = null;

    private static final String UNEXPORT = "/sys/class/gpio/unexport";
    private static final String EXPORT = "/sys/class/gpio/export";

    public GPIOPin(String name, int id, String direction, boolean debug)
    {
        mName = name;
        mPinID = id;
        mDirection = direction;
        mDebug = debug;

        initFilesystem();

        switch (direction)
        {
        case DIRECTION_IN:
            initInput();
            break;

        case DIRECTION_OUT:
            initOutput();
            break;

        default:
            Log.e(MainActivity.TAG, "Unknown direction for pin " + mName + ": " + direction);
            break;
        }
    }

    /**
     * Opens the required files for changing the pin states later on
     */
    private void initFilesystem()
    {
        RootUtils.enableRoot();

        unexportPin();
        exportPin();

        mFilename = "/sys/class/gpio/gpio" + mPinID + "/value";
        File valueFile = new File(mFilename);
        File directionFile = new File("/sys/class/gpio/gpio" + mPinID + "/direction");

        try (FileOutputStream directionFos = new FileOutputStream(directionFile))
        {
            // change permission of direction file to 777
            boolean result = true;
            result = result && directionFile.setWritable(true, false);
            result = result && directionFile.setExecutable(true, false);
            result = result && directionFile.setReadable(true, false);

            // change permission of value file to 777
            result = result && valueFile.setWritable(true, false);
            result = result && valueFile.setExecutable(true, false);
            result = result && valueFile.setReadable(true, false);
            if (!result) { Log.e(MainActivity.TAG, "Failed to change permission for pin " + mName); }

            // set direction
            directionFos.write(mDirection.getBytes());
            directionFos.flush();
        }
        catch(Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to export pin " + mName + ": " + e.getMessage());
        }

        RootUtils.disableRoot();
    }

    private void exportPin()
    {
        try (FileOutputStream exportFos = new FileOutputStream(new File(EXPORT)))
        {
            exportFos.write(mPinID.toString().getBytes());
            exportFos.flush();
        }
        catch (Exception ignored) { }
    }

    private void unexportPin()
    {
        try (FileOutputStream unexportFos = new FileOutputStream(new File(UNEXPORT)))
        {
            unexportFos.write(mPinID.toString().getBytes());
            unexportFos.flush();
        }
        catch (Exception ignored) { }
    }

    private void initInput()
    {
        Log.d(MainActivity.TAG, "Initializing pin " + mName + " as input");

        try
        {
            mInput = new RandomAccessFile(mFilename, "r");
            mInput.seek(0);
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to open pin " + mName + " for reading: " + e.getMessage());
        }
    }

    private void initOutput()
    {
        Log.d(MainActivity.TAG, "Initializing pin " + mName + " as output");

        try
        {
            mOutput = new FileOutputStream(mFilename);
            mOutput.write("0".getBytes());
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Failed to open pin " + mName + " for writing: " + e.getMessage());
        }
    }

    /**
     * Will set the pin to level when output
     */
    public void setValue(boolean level)
    {
        if (mOutput == null)
        {
            Log.e(MainActivity.TAG, "Failed to write into an uninitialized output pin " + mName);
            return;
        }

        char value = (level) ? '1' : '0';
        if (mDebug) { Log.d(MainActivity.TAG, "Setting pin " + mPinID + " " + mName + " to " + value); }

        try
        {
            mOutput.write(value);
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Can't set pin value for pin " + mName + ": " + e.getMessage());
        }
    }

    /**
     * Returns current pin level
     */
    public boolean getValue()
    {
        if (mInput == null)
        {
            Log.e(MainActivity.TAG, "Failed to read from an uninitialized input pin " + mName);
            return false;
        }

        try
        {
            mInput.seek(0);
            char value = (char) mInput.read();
            if (mDebug) { Log.d(MainActivity.TAG, "Pin " + mName + " has value of " + value); }

            return (value == '1');
        }
        catch (Exception e)
        {
            Log.e(MainActivity.TAG, "Can't get pin value for pin " + mName + ": " + e.getMessage());
            return false;
        }
    }

    public void cleanup()
    {
        RootUtils.enableRoot();
        unexportPin();
        RootUtils.disableRoot();
    }
}
