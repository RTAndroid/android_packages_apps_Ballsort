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

import java.util.HashMap;
import java.util.Map;

import rtandroid.ballsort.blocks.color.ColorType;

public class MeanColorClassifier implements IColorClassifier
{
    private static final HashMap<ColorType, int[]> AVERAGE_DATA = new HashMap<>();

    static
    {
        HashMap<int[], ColorType> trainingData = new HashMap<>();

        trainingData.put(new int[] {336,  347, 265}, ColorType.BLACK);
        trainingData.put(new int[] {347,  359, 275}, ColorType.BLACK);
        trainingData.put(new int[] {349,  354, 272}, ColorType.BLACK);
        trainingData.put(new int[] {352,  357, 274}, ColorType.BLACK);
        trainingData.put(new int[] {352,  363, 278}, ColorType.BLACK);
        trainingData.put(new int[] {361,  359, 276}, ColorType.BLACK);
        trainingData.put(new int[] {365,  361, 279}, ColorType.BLACK);
        trainingData.put(new int[] {367,  364, 279}, ColorType.BLACK);
        trainingData.put(new int[] {372,  367, 284}, ColorType.BLACK);
        trainingData.put(new int[] {373,  390, 298}, ColorType.BLACK);
        trainingData.put(new int[] {379,  373, 288}, ColorType.BLACK);

        trainingData.put(new int[] {566,  666, 705}, ColorType.BLUE);
        trainingData.put(new int[] {598,  687, 740}, ColorType.BLUE);
        trainingData.put(new int[] {605,  709, 705}, ColorType.BLUE);
        trainingData.put(new int[] {608,  706, 749}, ColorType.BLUE);
        trainingData.put(new int[] {648,  727, 785}, ColorType.BLUE);
        trainingData.put(new int[] {686,  795, 788}, ColorType.BLUE);
        trainingData.put(new int[] {689,  775, 817}, ColorType.BLUE);
        trainingData.put(new int[] {718,  835, 918}, ColorType.BLUE);
        trainingData.put(new int[] {724,  836, 792}, ColorType.BLUE);

        trainingData.put(new int[] {515,  655, 496}, ColorType.GREEN);
        trainingData.put(new int[] {570,  743, 471}, ColorType.GREEN);
        trainingData.put(new int[] {605,  765, 483}, ColorType.GREEN);
        trainingData.put(new int[] {628,  785, 483}, ColorType.GREEN);
        trainingData.put(new int[] {629,  802, 512}, ColorType.GREEN);
        trainingData.put(new int[] {634,  820, 511}, ColorType.GREEN);
        trainingData.put(new int[] {680,  828, 518}, ColorType.GREEN);
        trainingData.put(new int[] {690,  853, 533}, ColorType.GREEN);
        trainingData.put(new int[] {764, 1001, 624}, ColorType.GREEN);

        trainingData.put(new int[] {1004, 446, 345}, ColorType.RED);
        trainingData.put(new int[] {1065, 451, 352}, ColorType.RED);
        trainingData.put(new int[] {1093, 476, 366}, ColorType.RED);
        trainingData.put(new int[] {1159, 485, 377}, ColorType.RED);
        trainingData.put(new int[] {1194, 482, 376}, ColorType.RED);
        trainingData.put(new int[] {1234, 498, 380}, ColorType.RED);
        trainingData.put(new int[] {1260, 504, 394}, ColorType.RED);
        trainingData.put(new int[] {1263, 499, 391}, ColorType.RED);
        trainingData.put(new int[] {1367, 525, 409}, ColorType.RED);
        trainingData.put(new int[] {1372, 547, 428}, ColorType.RED);
        trainingData.put(new int[] {1386, 536, 420}, ColorType.RED);

        trainingData.put(new int[] {1684, 1764,  779}, ColorType.YELLOW);
        trainingData.put(new int[] {1727, 1815,  806}, ColorType.YELLOW);
        trainingData.put(new int[] {1920, 2014,  882}, ColorType.YELLOW);
        trainingData.put(new int[] {1968, 2034,  886}, ColorType.YELLOW);
        trainingData.put(new int[] {2072, 2156,  938}, ColorType.YELLOW);
        trainingData.put(new int[] {2126, 2181,  954}, ColorType.YELLOW);
        trainingData.put(new int[] {2215, 2251,  980}, ColorType.YELLOW);
        trainingData.put(new int[] {2266, 2276,  991}, ColorType.YELLOW);
        trainingData.put(new int[] {2342, 2338, 1020}, ColorType.YELLOW);
        trainingData.put(new int[] {2354, 2366, 1027}, ColorType.YELLOW);

        trainingData.put(new int[] {2019, 2386, 1686}, ColorType.WHITE);
        trainingData.put(new int[] {2082, 2466, 1780}, ColorType.WHITE);
        trainingData.put(new int[] {2195, 2587, 1834}, ColorType.WHITE);
        trainingData.put(new int[] {2436, 2855, 2031}, ColorType.WHITE);
        trainingData.put(new int[] {2461, 2857, 2007}, ColorType.WHITE);
        trainingData.put(new int[] {2645, 3089, 2194}, ColorType.WHITE);
        trainingData.put(new int[] {2679, 3055, 2166}, ColorType.WHITE);
        trainingData.put(new int[] {2687, 3101, 2203}, ColorType.WHITE);
        trainingData.put(new int[] {2877, 3294, 2362}, ColorType.WHITE);
        trainingData.put(new int[] {2935, 3322, 2376}, ColorType.WHITE);

        AVERAGE_DATA.clear();
        for (ColorType color : ColorType.values())
        {
            if (color.equals(ColorType.EMPTY)) { continue; }

            int count = 0;
            int r = 0;
            int g = 0;
            int b = 0;

            for (Map.Entry<int[], ColorType> data : trainingData.entrySet())
            {
                if (data.getValue().equals(color))
                {
                    int[] rgb = data.getKey();
                    r += rgb[0];
                    g += rgb[1];
                    b += rgb[2];
                    count++;
                }
            }

            r /= count;
            g /= count;
            b /= count;

            int[] rgb = new int[] {r, g, b};
            AVERAGE_DATA.put(color, rgb);
        }
    }

    @Override
    public String getName()
    {
        return MeanColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorType classify(int r, int g, int b)
    {
        double minDistance = Long.MAX_VALUE;
        ColorType minName = ColorType.BLACK;

        for (Map.Entry<ColorType, int[]> entry : AVERAGE_DATA.entrySet())
        {
            int[] rgb = entry.getValue();
            long rd = r - rgb[0];
            long gd = g - rgb[1];
            long bd = b - rgb[2];

            double distance = Math.sqrt(rd * rd + gd * gd + bd * bd);
            if (distance >= minDistance) { continue; }

            minDistance = distance;
            minName = entry.getKey();
        }

        return minName;
    }
}
