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

import java.io.Serializable;

import static rtandroid.ballsort.settings.Constants.PATTERN_COLUMNS_COUNT;
import static rtandroid.ballsort.settings.Constants.PATTERN_COLUMNS_SIZE;

public class Settings implements Serializable
{
    public int StepperEnableDelay = 1;
    public int StepperDisableDelay = 1;

    public final int FeederStepperPwmDelay = 750;
    public int FeederValveDropDelay =  500;

    public final int SlingshotStepperPwmDelay = 450;
    public final int SlingshotDelayAfterRotate = 300;

    public final int SlingshotValveOpenDelay = 100;
    public final int SlingshotDelayBeforeShoot = 300;
    public final long SlingshotErrorThreshold =  700000;
    public final long SlingshotFreeThreshold = 2200000;

    public final int BallsToIgnoreAtReset = 3;
    public final int BeforeDropDelay = 100;
    public final int AfterDropDelay = 500;
    public final int BusyWaitDelay = 1;

    public final int BaseDelayMs = 54;
    public final int[] ColumnDelaysUs = { 4400, 5050, 7000, 7800, 8500, 9220 };

    public final int ColorSersorRepeats = 10;
    public final int ColorSensorDelay = 50;

    public final int ColorLightColorThreshold = 1350;
    public final int ColorBlackThreshold = 580;
    public final double ColorYellowThreshold = 0.8;

    public final int[][] Pattern = new int[PATTERN_COLUMNS_COUNT][PATTERN_COLUMNS_SIZE];
}
