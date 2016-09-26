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

package rtandroid.ballsort.blocks.color.classifier;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

public class TreeColorClassifier implements IColorClassifier
{
    @Override
    public String getName()
    {
        return TreeColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorType classify(int r, int g, int b)
    {
        Settings settings = SettingsManager.getSettings();
        ColorType detected = ColorType.BLACK;

        long colSum = r + g + b;
        long colMean = colSum / 3;
//        Log.d(MainActivity.TAG, "colMean is: "+colMean);

        // light color
        if (colMean > settings.ColorLightColorThreshold)
        {
            double bDivColMean = (double) b / colMean;
            if (bDivColMean > settings.ColorYellowThreshold)
            {
                detected = ColorType.WHITE;
            }
            else
            {
                detected = ColorType.YELLOW;
            }
        // dark color
        }
        else
        {
            // black
            if(colMean < settings.ColorBlackThreshold)
            {
                detected = ColorType.BLACK;
            }
            else
            {
                long maxColValue = 0;

                if (r > maxColValue)
                {
                    detected = ColorType.RED;
                    maxColValue = r;
                }
                if (g > maxColValue)
                {
                    detected = ColorType.GREEN;
                    maxColValue = g;
                }
                if (b > maxColValue)
                {
                    detected = ColorType.BLUE;
                }
            }

        }

        return detected;
    }
}
