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

package rtandroid.ballsort.blocks.color.space;

public class ColorRGB
{
    public int R = 0;
    public int G = 0;
    public int B = 0;

    public ColorRGB(int r, int g, int b)
    {
        R = r;
        G = g;
        B = b;
    }

    @Override
    public String toString()
    {
        return "RGB { " + R + ", " + G + ", " + B + " }";
    }
}
