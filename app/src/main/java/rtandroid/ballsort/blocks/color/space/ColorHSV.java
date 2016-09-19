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

public class ColorHSV
{
    public int H = 0;
    public double S = 0;
    public double V = 0;

    public ColorHSV(int h, double s, double v)
    {
        H = h;
        S = s;
        V = v;
    }

    @Override
    public String toString()
    {
        return "HSV { " + H + ", " + S + ", " + V + " }";
    }
}
