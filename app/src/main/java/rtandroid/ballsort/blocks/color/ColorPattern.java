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

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.hardware.pins.GPIOPin;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

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
        DataState data = SettingsManager.getData();
        data.PatternState = Constants.BLOCK_STOPPED;
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
    public int getNextColumn(ColorData color)
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();

        if (mIgnoredBalls < settings.BallsToIgnoreAtReset)
        {
            mIgnoredBalls++;

            data.PatternState = "IGNORING";
            return Constants.PATTERN_COLUMNS_COUNT - 1;
        }

        if (mShouldOpenPins == 0) { mShouldOpenPins = 1; }
        
        data.PatternState = "BUILDING";
        // ignore empty spaces
        if(color.equals(ColorData.EMPTY)) { return SKIP; }

        int col = color.getDefaultColumn();
        if (mFillings[col] >= Constants.PATTERN_COLUMNS_SIZE)return SKIP;
        mFillings[col]++;

        // TODO: remove
        if (1 == 1) { return col; }

        for (int row = 0; row < Constants.PATTERN_COLUMNS_COUNT; row++)
        {
            int place = mFillings[row];
            if(place >= Constants.PATTERN_COLUMNS_SIZE) { continue; }
            if (settings.Pattern[row][place] == SKIP) { continue; }
            if (settings.Pattern[row][place] == color.getPaintColor() && place < Constants.PATTERN_COLUMNS_SIZE)
            {
                mFillings[row]++;
                return row;
            }
        }

        return SKIP;
    }

    /**
     * Will check if another ball is needed to complete the Pattern
     * @return boolean result if full
     */
    public boolean isFull()
    {
        //TODO remove tmp solution
        boolean full = true;

        for (int row = 0; row < Constants.PATTERN_COLUMNS_COUNT; row++)
        {
            int space = mFillings[row];

            // hack for missing blue balls
            if(space == 0 && row == ColorData.BLUE.getDefaultColumn()){ continue; }
            if (space >= Constants.PATTERN_COLUMNS_SIZE) { continue; }

            // else
            full = false;
        }
        if(full) { Log.d(MainActivity.TAG, "Pattern is full!"); }
        return full;

        /*
        int[][] pattern = SettingsManager.getSettings().Pattern;
        boolean full = true;

        for (int row = 0; row < Constants.PATTERN_COLUMNS_COUNT; row++)
        {
            int space = mFillings[row];

            // next slot in this row should be empty
            if (space < Constants.PATTERN_COLUMNS_SIZE && pattern[row][space] == SKIP) { continue; }

            // row is already full
            if (space >= Constants.PATTERN_COLUMNS_SIZE) { continue; }

            // else
            full = false;
        }
        return full;
        */
    }

    public void preparePins()
    {
        // TODO: WTH is 0,1,2?
        if (mShouldOpenPins == 1)
        {
            Settings settings = SettingsManager.getSettings();
            mBottomPins.setValue(true);
            mShouldOpenPins = 2;
        }
    }
}
