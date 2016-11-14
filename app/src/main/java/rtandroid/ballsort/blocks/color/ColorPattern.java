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
import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.hardware.pins.GPIOPin;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

public class ColorPattern
{
    public static final int SKIP = -1;

    protected GPIOPin mBottomPins = null;
    protected int[] mFillings = null;

    private int mIgnoredBalls = 0;
    private int mShouldOpenPins = 0;

    public ColorPattern()
    {
        mBottomPins = new GPIOPin("ColorPatternReset", Constants.PATTERN_VALVE_PIN, GPIOPin.DIRECTION_OUT, true);
        mFillings = new int[Constants.PATTERN_COLUMNS_COUNT];
    }

    public void cleanup()
    {
        mBottomPins.cleanup();
    }

    public void resetPattern()
    {
        mBottomPins.setValue(false);
        mIgnoredBalls = 0;
        mShouldOpenPins = 0;
    }

    /**
     * This will map an upcoming ball to its corresponding location in the pattern.
     *
     * @param color The color of the upcoming ball
     * @return The number of the row, that ball belongs to or -1 if it can't be of use now
     */
    public int getNextColumn(ColorType color)
    {
        Settings settings = SettingsManager.getSettings();

        if (mIgnoredBalls < settings.BallsToIgnoreAtReset)
        {
            mIgnoredBalls++;
            return Constants.PATTERN_COLUMNS_COUNT - 1;
        }

        if (mShouldOpenPins == 0) { mShouldOpenPins = 1; }
        
        // ignore unknown balls
        if (color.equals(ColorType.EMPTY)) { return SKIP; }

        for (int col = 0; col < Constants.PATTERN_COLUMNS_COUNT; col++)
        {
            int place = mFillings[col];
            if(place >= Constants.PATTERN_COLUMNS_SIZE) { continue; }
            if (settings.Pattern[col][place] == ColorType.EMPTY) { continue; }
            if (settings.Pattern[col][place] == color && place < Constants.PATTERN_COLUMNS_SIZE)
            {
                return col;
            }
        }

        return SKIP;
    }

    public void onBallDropped(int col)
    {
        if(col < 0 || col >= mFillings.length) { return; }
        mFillings[col]++;
        DataState data = SettingsManager.getData();
        data.mFillings[col]++;
    }

    /**
     * Will check if another ball is needed to complete the Pattern
     * @return boolean result if full
     */
    public boolean isFull()
    {

        ColorType[][] pattern = SettingsManager.getSettings().Pattern;
        boolean full = true;

        for (int row = 0; row < Constants.PATTERN_COLUMNS_COUNT; row++)
        {
            int space = mFillings[row];

            // next slot in this row should be empty
            if (space < Constants.PATTERN_COLUMNS_SIZE && pattern[row][space] == ColorType.EMPTY) { continue; }

            // row is already full
            if (space >= Constants.PATTERN_COLUMNS_SIZE) { continue; }

            // else
            full = false;
        }
        return full;

    }

    public void preparePins()
    {
        // TODO: WTH is 0,1,2?
        if (mShouldOpenPins == 1)
        {
            mBottomPins.setValue(true);
            mShouldOpenPins = 2;
        }
    }
}
