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

package rtandroid.ballsort.blocks.color.classifier;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorRGB;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

public class TreeColorClassifier implements IColorClassifier
{
    private static final boolean DEBUG = false;

    @Override
    public String getName()
    {
        return TreeColorClassifier.class.getSimpleName();
    }

    private ColorType classifyLight(ColorRGB color, double mean)
    {
        Settings settings = SettingsManager.getSettings();

        double bDivMean = (double) color.b / mean;
        if (DEBUG) { Log.d(MainActivity.TAG, getName() + ": b div = " + bDivMean); }

        if (bDivMean > settings.ColorYellowThreshold)
        {
            return ColorType.WHITE;
        }
        else
        {
            return ColorType.YELLOW;
        }
    }

    private ColorType classifyDark(ColorRGB color)
    {
        ColorType detected = ColorType.EMPTY;
        double maxColValue = 0;

        if (color.r > maxColValue)
        {
            detected = ColorType.RED;
            maxColValue = color.r;
        }
        if (color.g > maxColValue)
        {
            detected = ColorType.GREEN;
            maxColValue = color.g;
        }
        if (color.b > maxColValue)
        {
            detected = ColorType.BLUE;
        }

        return detected;
    }

    @Override
    public ColorType classify(ColorRGB color)
    {
        Settings settings = SettingsManager.getSettings();

        double colSum = color.r + color.g + color.b;
        double colMean = colSum / 3;
        if (DEBUG) { Log.d(MainActivity.TAG, getName() + ": mean = " + colMean); }

        // light color
        if (colMean > settings.ColorLightColorThreshold)
        {
            return classifyLight(color, colMean);
        }
        // black is detected separately
        else if (colMean < settings.ColorBlackThreshold)
        {
            return ColorType.BLACK;
        }
        // dark color
        else
        {
            return classifyDark(color);
        }
    }
}
