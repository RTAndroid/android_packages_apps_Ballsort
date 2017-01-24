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

package rtandroid.ballsort.blocks.color;

import android.graphics.Color;

public enum ColorType
{
    BLACK  (Color.rgb(0,0,0)),
    BLUE   (Color.rgb(0,84,159)),
    GREEN  (Color.rgb(87,171,39)),
    RED    (Color.rgb(204,7,30)),
    YELLOW (Color.rgb(255,237,0)),
    WHITE  (Color.rgb(236,237,237)),
    EMPTY  (Color.GRAY);

    private final int mColor;

    ColorType(int color)
    {
        mColor = color;
    }

    public int getColor() { return mColor; }

    public static final String[] names = new String[values().length];
    static { for (ColorType ct : values()) { names[ct.ordinal()] = ct.name(); } }
}
