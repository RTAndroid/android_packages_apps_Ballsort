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

import java.util.HashMap;
import java.util.Map;

import rtandroid.ballsort.blocks.color.ColorRGB;
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

        trainingData.put(new int[] {733,  979, 1035}, ColorType.BLUE);
        trainingData.put(new int[] {738,  984, 1008}, ColorType.BLUE);
        trainingData.put(new int[] {741,  991, 1026}, ColorType.BLUE);
        trainingData.put(new int[] {753, 1010, 1060}, ColorType.BLUE);
        trainingData.put(new int[] {780, 1039, 1081}, ColorType.BLUE);
        trainingData.put(new int[] {784, 1041, 1080}, ColorType.BLUE);
        trainingData.put(new int[] {784, 1046, 1123}, ColorType.BLUE);
        trainingData.put(new int[] {787, 1044, 1086}, ColorType.BLUE);
        trainingData.put(new int[] {792, 1054, 1103}, ColorType.BLUE);
        trainingData.put(new int[] {803, 1069, 1120}, ColorType.BLUE);

        trainingData.put(new int[] {767, 1033, 733}, ColorType.GREEN);
        trainingData.put(new int[] {796, 1064, 749}, ColorType.GREEN);
        trainingData.put(new int[] {797, 1069, 752}, ColorType.GREEN);
        trainingData.put(new int[] {800, 1070, 749}, ColorType.GREEN);
        trainingData.put(new int[] {800, 1072, 756}, ColorType.GREEN);
        trainingData.put(new int[] {808, 1076, 755}, ColorType.GREEN);
        trainingData.put(new int[] {817, 1086, 763}, ColorType.GREEN);
        trainingData.put(new int[] {822, 1093, 763}, ColorType.GREEN);
        trainingData.put(new int[] {824, 1098, 769}, ColorType.GREEN);
        trainingData.put(new int[] {832, 1096, 761}, ColorType.GREEN);

        trainingData.put(new int[] {1272, 889, 696}, ColorType.RED);
        trainingData.put(new int[] {1280, 884, 692}, ColorType.RED);
        trainingData.put(new int[] {1265, 880, 691}, ColorType.RED);
        trainingData.put(new int[] {1244, 873, 683}, ColorType.RED);

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
    public ColorType classify(ColorRGB color)
    {
        double minDistance = Long.MAX_VALUE;
        ColorType minName = ColorType.BLACK;

        for (Map.Entry<ColorType, int[]> entry : AVERAGE_DATA.entrySet())
        {
            int[] rgb = entry.getValue();
            long rd = color.r - rgb[0];
            long gd = color.g - rgb[1];
            long bd = color.b - rgb[2];

            double distance = Math.sqrt(rd * rd + gd * gd + bd * bd);
            if (distance >= minDistance) { continue; }

            minDistance = distance;
            minName = entry.getKey();
        }

        return minName;
    }
}
