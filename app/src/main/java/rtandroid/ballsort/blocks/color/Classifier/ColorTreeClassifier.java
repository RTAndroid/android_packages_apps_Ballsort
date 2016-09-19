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

package rtandroid.ballsort.blocks.color.Classifier;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.ColorRGB;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

public class ColorTreeClassifier
{
    public static ColorData classify(ColorRGB color)
    {
        Settings settings = SettingsManager.getSettings();

        long colSum = color.R + color.G + color.B;
        ColorData detected = ColorData.BLACK;

        long colMean = colSum / 3;
        Log.d(MainActivity.TAG, "colMean is: "+colMean);

        // light color
        if (colMean > settings.ColorLightColorThreshold)
        {
            double bDivColMean = (double) color.B / colMean;
            if (bDivColMean > settings.ColorYellowThreshold)
            {
                detected = ColorData.WHITE;
            }
            else
            {
                detected = ColorData.YELLOW;
            }
        // dark color
        }
        else
        {
            // black
            if(colMean < settings.ColorBlackThreshold)
            {
                detected = ColorData.BLACK;
            }
            else
            {
                long maxColValue =0;

                if (color.R > maxColValue)
                {
                    detected = ColorData.RED;
                    maxColValue = color.R;
                }
                if (color.G > maxColValue)
                {
                    detected = ColorData.GREEN;
                    maxColValue=color.G;
                }
                if (color.B > maxColValue)
                {
                    detected = ColorData.BLUE;
                }
            }

        }
        Log.d(MainActivity.TAG, "Detected color: " + color.toString() + " -> " + detected.name().toLowerCase());
        return detected;
    }
}
