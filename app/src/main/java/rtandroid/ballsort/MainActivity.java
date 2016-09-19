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

package rtandroid.ballsort;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import rtandroid.ballsort.blocks.AStateBlock;
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.gui.ColorView;
import rtandroid.ballsort.hardware.Sorter;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class MainActivity extends Activity
{
    public static final String TAG = "Ballsort";
    private static final int REFRESH_RATE_MS = 200;

    private final Handler mUiUpdateHandler = new Handler();
    private final Runnable mUiUpdateRunnable = new Runnable()
    {
        @Override public void run() { updateUi(); }
    };

    private enum LoopType { LOOP_SORT, LOOP_RESET }

    private AStateBlock mCurrentSortLoop = null;
    private AStateBlock mCurrentResetLoop = null;

    private Switch mSwchSort = null;
    private Switch mSwchReset = null;
    private ColorView mCvQueued = null;
    private ColorView mCvNext = null;
    private ColorView mCvDrop = null;
    private TextView mTvBallsDropped = null;
    private TextView mTvFreeMemory = null;

    private TextView mTvSlingshotState = null;
    private TextView mTvSlingshotMotorState = null;
    private TextView mTvFeederState = null;
    private TextView mTvPatternState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCvQueued = (ColorView) findViewById(R.id.cvQueuedBall);
        mCvNext = (ColorView) findViewById(R.id.cvNextBall);
        mCvDrop = (ColorView) findViewById(R.id.cvDropBall);

        mTvBallsDropped = (TextView) findViewById(R.id.tvBallsDropped);
        mTvFreeMemory = (TextView) findViewById(R.id.tvFreeMemory);
        mTvSlingshotState = (TextView) findViewById(R.id.tvSlingshotState);
        mTvSlingshotMotorState = (TextView) findViewById(R.id.tvSlingshotMotorState);
        mTvFeederState = (TextView) findViewById(R.id.tvFeederState);
        mTvPatternState = (TextView) findViewById(R.id.tvPatternState);

        mSwchSort = (Switch) findViewById(R.id.swSort);
        mSwchReset = (Switch) findViewById(R.id.swReset);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SettingsManager.writeToFile();
                Log.i(TAG, "Settings saved");
            }
        });

        Button btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SettingsManager.readFromFile();
                Log.i(TAG, "Settings loaded");
            }
        });

        mSwchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) { startMainLoop(LoopType.LOOP_SORT); }
                else
                {
                    stopLoop(mCurrentSortLoop);
                    mCurrentSortLoop = null;
                }
            }
        });

        mSwchReset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) { startMainLoop(LoopType.LOOP_RESET); }
                else
                {
                    stopLoop(mCurrentResetLoop);
                    mCurrentResetLoop = null;
                }
            }
        });

        // system setup
        Sorter.extract(this);
        Sorter.load();

        // Create two temporary loops to initialise the needed hardware
        Log.i(TAG, "Initialising Hardware");
        mCurrentSortLoop = new SortLoop();
        mCurrentResetLoop = new ResetLoop();
    }

    private void startMainLoop(LoopType type)
    {
        Log.i(TAG, "Starting the main execution");

        // system setup
        Utils.lockPower();

        // create the correct loop type
        switch (type)
        {
        case LOOP_SORT:
            mCurrentSortLoop = new SortLoop();
            mSwchReset.setEnabled(false);
            mCurrentSortLoop.start();
            break;

        case LOOP_RESET:
            mCurrentResetLoop = new ResetLoop();
            mSwchSort.setEnabled(false);
            mCurrentResetLoop.start();
            break;
        }
        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }

    private void updateUi()
    {
        DataState data = SettingsManager.getData();

        mCvQueued.setColor(data.mQueuedColor.getPaintColor());
        mCvNext.setColor(data.mNextColor.getPaintColor());
        mCvDrop.setColor(data.mDropColor.getPaintColor());

        mTvSlingshotState.setText("Slingshot LightSwitch: " + data.SlingshotState + "");
        mTvSlingshotMotorState.setText("Slingshot Motor: " + data.SlingshotMotorState + "");
        mTvFeederState.setText("Feeder state: " + data.FeederState + "");
        mTvPatternState.setText("Pattern state: " + data.PatternState + "");

        Runtime rt = Runtime.getRuntime();
        long freeKB = rt.freeMemory() / 1024;
        long totalKB = rt.totalMemory() / 1024;
        mTvFreeMemory.setText("Free memory: " + freeKB + " kb / " + totalKB + " kb");
        mTvBallsDropped.setText("Dropped: " + Sorter.getBallCount() + "");

        // if the loops terminated change the switch
        if (mCurrentSortLoop!= null && !mCurrentSortLoop.isRunning()) { mSwchSort.setChecked(false); }
        if (mCurrentResetLoop!= null && !mCurrentResetLoop.isRunning()) { mSwchReset.setChecked(false); }

        // the execution terminates eventually
        if (mCurrentSortLoop == null && mCurrentResetLoop == null) { return; }
        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }

    private void stopLoop(AStateBlock loop)
    {
        Log.i(TAG, "Stopping the main execution");

        if (loop == null) { return; }
        loop.terminate();
        loop.waitFor();

        // system setup
        Utils.unlockPower();

        // re-enable UI elements
        mSwchSort.setEnabled(true);
        mSwchReset.setEnabled(true);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Sorter.unload();

        stopLoop(mCurrentResetLoop);
        stopLoop(mCurrentSortLoop);
    }
}
