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

package rtandroid.ballsort.blocks.color.classifeir;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorConverter;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.space.ColorHSV;
import rtandroid.ballsort.blocks.color.space.ColorLAB;
import rtandroid.ballsort.blocks.color.space.ColorRGB;

public class NeuronalColorClassifier implements IColorClassifier
{
    @Override
    public String getName()
    {
        return NeuronalColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorData classify(ColorRGB color)
    {
        double minDistance = Long.MAX_VALUE;
        ColorData minName = ColorData.EMPTY;

        ColorHSV colorHSV = ColorConverter.rgb2hsv(color);
        ColorLAB colorLAB = ColorConverter.rgb2lab(color);
        Log.d(MainActivity.TAG, color.toString() + " - " + colorHSV.toString() + "-" + colorLAB.toString());

        return minName;
    }
}
