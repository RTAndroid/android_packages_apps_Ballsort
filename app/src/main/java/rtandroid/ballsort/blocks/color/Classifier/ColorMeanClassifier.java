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

import java.util.HashMap;
import java.util.Map;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.ColorRGB;

public class ColorMeanClassifier
{
    private static final HashMap<ColorData, ColorRGB> AVERAGE_DATA = new HashMap<>();

    static
    {
        HashMap<ColorRGB, ColorData> trainingData = new HashMap<>();

        trainingData.put(new ColorRGB(336,  347, 265), ColorData.BLACK);
        trainingData.put(new ColorRGB(347,  359, 275), ColorData.BLACK);
        trainingData.put(new ColorRGB(349,  354, 272), ColorData.BLACK);
        trainingData.put(new ColorRGB(352,  357, 274), ColorData.BLACK);
        trainingData.put(new ColorRGB(352,  363, 278), ColorData.BLACK);
        trainingData.put(new ColorRGB(361,  359, 276), ColorData.BLACK);
        trainingData.put(new ColorRGB(365,  361, 279), ColorData.BLACK);
        trainingData.put(new ColorRGB(367,  364, 279), ColorData.BLACK);
        trainingData.put(new ColorRGB(372,  367, 284), ColorData.BLACK);
        trainingData.put(new ColorRGB(373,  390, 298), ColorData.BLACK);
        trainingData.put(new ColorRGB(379,  373, 288), ColorData.BLACK);

        trainingData.put(new ColorRGB(566,  666, 705), ColorData.BLUE);
        trainingData.put(new ColorRGB(598,  687, 740), ColorData.BLUE);
        trainingData.put(new ColorRGB(605,  709, 705), ColorData.BLUE);
        trainingData.put(new ColorRGB(608,  706, 749), ColorData.BLUE);
        trainingData.put(new ColorRGB(648,  727, 785), ColorData.BLUE);
        trainingData.put(new ColorRGB(686,  795, 788), ColorData.BLUE);
        trainingData.put(new ColorRGB(689,  775, 817), ColorData.BLUE);
        trainingData.put(new ColorRGB(718,  835, 918), ColorData.BLUE);
        trainingData.put(new ColorRGB(724,  836, 792), ColorData.BLUE);

        trainingData.put(new ColorRGB(515,  655, 496), ColorData.GREEN);
        trainingData.put(new ColorRGB(570,  743, 471), ColorData.GREEN);
        trainingData.put(new ColorRGB(605,  765, 483), ColorData.GREEN);
        trainingData.put(new ColorRGB(628,  785, 483), ColorData.GREEN);
        trainingData.put(new ColorRGB(629,  802, 512), ColorData.GREEN);
        trainingData.put(new ColorRGB(634,  820, 511), ColorData.GREEN);
        trainingData.put(new ColorRGB(680,  828, 518), ColorData.GREEN);
        trainingData.put(new ColorRGB(690,  853, 533), ColorData.GREEN);
        trainingData.put(new ColorRGB(764, 1001, 624), ColorData.GREEN);

        trainingData.put(new ColorRGB(1004, 446, 345), ColorData.RED);
        trainingData.put(new ColorRGB(1065, 451, 352), ColorData.RED);
        trainingData.put(new ColorRGB(1093, 476, 366), ColorData.RED);
        trainingData.put(new ColorRGB(1159, 485, 377), ColorData.RED);
        trainingData.put(new ColorRGB(1194, 482, 376), ColorData.RED);
        trainingData.put(new ColorRGB(1234, 498, 380), ColorData.RED);
        trainingData.put(new ColorRGB(1260, 504, 394), ColorData.RED);
        trainingData.put(new ColorRGB(1263, 499, 391), ColorData.RED);
        trainingData.put(new ColorRGB(1367, 525, 409), ColorData.RED);
        trainingData.put(new ColorRGB(1372, 547, 428), ColorData.RED);
        trainingData.put(new ColorRGB(1386, 536, 420), ColorData.RED);

        trainingData.put(new ColorRGB(1684, 1764,  779), ColorData.YELLOW);
        trainingData.put(new ColorRGB(1727, 1815,  806), ColorData.YELLOW);
        trainingData.put(new ColorRGB(1920, 2014,  882), ColorData.YELLOW);
        trainingData.put(new ColorRGB(1968, 2034,  886), ColorData.YELLOW);
        trainingData.put(new ColorRGB(2072, 2156,  938), ColorData.YELLOW);
        trainingData.put(new ColorRGB(2126, 2181,  954), ColorData.YELLOW);
        trainingData.put(new ColorRGB(2215, 2251,  980), ColorData.YELLOW);
        trainingData.put(new ColorRGB(2266, 2276,  991), ColorData.YELLOW);
        trainingData.put(new ColorRGB(2342, 2338, 1020), ColorData.YELLOW);
        trainingData.put(new ColorRGB(2354, 2366, 1027), ColorData.YELLOW);

        trainingData.put(new ColorRGB(2019, 2386, 1686), ColorData.WHITE);
        trainingData.put(new ColorRGB(2082, 2466, 1780), ColorData.WHITE);
        trainingData.put(new ColorRGB(2195, 2587, 1834), ColorData.WHITE);
        trainingData.put(new ColorRGB(2436, 2855, 2031), ColorData.WHITE);
        trainingData.put(new ColorRGB(2461, 2857, 2007), ColorData.WHITE);
        trainingData.put(new ColorRGB(2645, 3089, 2194), ColorData.WHITE);
        trainingData.put(new ColorRGB(2679, 3055, 2166), ColorData.WHITE);
        trainingData.put(new ColorRGB(2687, 3101, 2203), ColorData.WHITE);
        trainingData.put(new ColorRGB(2877, 3294, 2362), ColorData.WHITE);
        trainingData.put(new ColorRGB(2935, 3322, 2376), ColorData.WHITE);

        AVERAGE_DATA.clear();
        for (ColorData color : ColorData.values())
        {
            int count = 0;
            int r = 0;
            int g = 0;
            int b = 0;

            if(color.equals(ColorData.EMPTY)) { continue; }

            for (Map.Entry<ColorRGB, ColorData> data : trainingData.entrySet()){
                if (data.getValue().equals(color))
                {
                    r += data.getKey().R;
                    g += data.getKey().G;
                    b += data.getKey().B;
                    count++;
                }
            }

            r /= count;
            g /= count;
            b /= count;
            ColorRGB data = new ColorRGB(r, g, b);

            AVERAGE_DATA.put(color, data);
        }
    }

    public static ColorData classify(ColorRGB color)
    {
        double minDistance = Long.MAX_VALUE;
        ColorData minName = ColorData.BLACK;

        for (Map.Entry<ColorData, ColorRGB> entry : AVERAGE_DATA.entrySet())
        {
            long rd = color.R - entry.getValue().R;
            long gd = color.G - entry.getValue().G;
            long bd = color.B - entry.getValue().B;

            double distance = Math.sqrt(rd * rd + gd * gd + bd * bd);
            if (distance >= minDistance) { continue; }

            minDistance = distance;
            minName = entry.getKey();
        }

        if(minName.equals(ColorData.YELLOW))
        {
            minName = ColorTreeClassifier.classify(color);
        }

        Log.d(MainActivity.TAG, "Detected color: " + color.toString() + " -> " + minName.name().toLowerCase());
        return minName;
    }
}
