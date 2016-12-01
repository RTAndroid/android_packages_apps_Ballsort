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

package rtandroid.ballsort.settings;

import rtandroid.ballsort.blocks.color.ColorType;

public class DataState
{
    public int mDetectedBalls = 0;

    public ColorType mQueuedColor = ColorType.EMPTY;
    public ColorType mDetectedColor = ColorType.EMPTY;
    public ColorType mDropColor = ColorType.EMPTY;
    public int[] mFillings = new int[Constants.PATTERN_COLUMNS_COUNT];

    public String SlingshotValveState = "UNKNOWN";
    public String FeederState = "UNKNOWN";
    public String SlingshotMotorState = "UNKNOWN";
    public String mModuleError = "";
}
