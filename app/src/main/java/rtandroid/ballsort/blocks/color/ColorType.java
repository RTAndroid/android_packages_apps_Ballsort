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

package rtandroid.ballsort.blocks.color;

import android.graphics.Color;

public enum ColorType
{
    BLACK(Color.rgb(0,0,0), Color.rgb(100,101,103)),
    BLUE(Color.rgb(0,84,159), Color.rgb(64,127,183)),
    GREEN(Color.rgb(87,171,39), Color.rgb(141,192,96)),
    RED(Color.rgb(204,7,30), Color.rgb(216,92,65)),
    YELLOW(Color.rgb(255,237,0), Color.rgb(255,240,85)),
    WHITE(Color.rgb(236,237,237), Color.rgb(207, 209, 210)),
    EMPTY(Color.rgb(101,101,103), Color.rgb(156,158,159));

    private int mPrimary = Color.GRAY;
    private int mSecondary = Color.GRAY;

    ColorType(int primary, int secondary)
    {
        mPrimary = primary;
        mSecondary = secondary;
    }

    public int getPrimaryColor() { return mPrimary; }
    public int getSecondaryColor() { return mSecondary; }
    public int getDefaultColumn() { return ordinal(); }

    public static final String[] names = new String[values().length];
    static
    {
        for(ColorType ct : values())
        {
            names[ct.ordinal()] = ct.name();
        }
    }
}
