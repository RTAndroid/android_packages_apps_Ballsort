package rtandroid.ballsort.gui.fragments;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.blocks.AStateBlock;
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.gui.ColorView;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class ControlFragment extends Fragment
{
    private static final int REFRESH_RATE_MS = 500;

    private final Handler mUiUpdateHandler = new Handler();
    private final Runnable mUiUpdateRunnable = this::updateUi;

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

    private TextView mTvFeederState = null;
    private TextView mTvSlingshotValveState = null;
    private TextView mTvSlingshotMotorState = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCvQueued = (ColorView) getView().findViewById(R.id.cvQueuedBall);
        mCvNext = (ColorView) getView().findViewById(R.id.cvNextBall);
        mCvDrop = (ColorView) getView().findViewById(R.id.cvDropBall);

        mTvBallsDropped = (TextView) getView().findViewById(R.id.tvBallsDropped);
        mTvFreeMemory = (TextView) getView().findViewById(R.id.tvFreeMemory);
        mTvFeederState = (TextView) getView().findViewById(R.id.tvFeederState);
        mTvSlingshotValveState = (TextView) getView().findViewById(R.id.tvSlingshotValveState);
        mTvSlingshotMotorState = (TextView) getView().findViewById(R.id.tvSlingshotMotorState);

        mSwchSort = (Switch) getView().findViewById(R.id.swSort);
        mSwchReset = (Switch) getView().findViewById(R.id.swReset);


        mSwchSort.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) { startMainLoop(LoopType.LOOP_SORT); }
            else
            {
                stopLoop(mCurrentSortLoop);
                mCurrentSortLoop = null;
            }
        });

        mSwchReset.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) { startMainLoop(LoopType.LOOP_RESET); }
            else
            {
                stopLoop(mCurrentResetLoop);
                mCurrentResetLoop = null;
            }
        });

        // create two temporary loops to initialise the needed hardware
        Log.i(MainActivity.TAG, "Initialising hardware state...");
        mCurrentSortLoop = new SortLoop();
        mCurrentResetLoop = new ResetLoop();
    }




    private void startMainLoop(LoopType type)
    {
        Log.i(MainActivity.TAG, "Starting the main execution");

        // make sure no other thread is running
        stopLoop(mCurrentSortLoop);
        stopLoop(mCurrentResetLoop);

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

        mCvQueued.setColor(data.mDetectedColor.getPaintColor());
        mCvNext.setColor(data.mQueuedColor.getPaintColor());
        mCvDrop.setColor(data.mDropColor.getPaintColor());

        mTvSlingshotValveState.setText("Slingshot Valve: " + data.SlingshotValveState + "");
        mTvSlingshotMotorState.setText("Slingshot Motor: " + data.SlingshotMotorState + "");
        mTvFeederState.setText("Feeder state: " + data.FeederState + "");

        Runtime rt = Runtime.getRuntime();
        long freeKB = rt.freeMemory() / 1024;
        long totalKB = rt.totalMemory() / 1024;
        mTvFreeMemory.setText("Free memory: " + freeKB + " kb / " + totalKB + " kb");
        mTvBallsDropped.setText("Dropped: " + data.mDetectedBalls + " balls");

        // if the loops terminated change the switch
        if (mCurrentSortLoop != null  && !mCurrentSortLoop.isRunning()) { mSwchSort.setChecked(false); }
        if (mCurrentResetLoop != null && !mCurrentResetLoop.isRunning()) { mSwchReset.setChecked(false); }

        // the execution terminates eventually
        if (mCurrentSortLoop == null && mCurrentResetLoop == null) { return; }
        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }

    private void stopLoop(AStateBlock loop)
    {
        if (loop == null) { return; }

        Log.i(MainActivity.TAG, "Stopping the main execution");
        loop.terminate();
        loop.waitFor();

        // make sure there is nothing running
        if (loop == mCurrentSortLoop) { mCurrentSortLoop = null; }
        if (loop == mCurrentResetLoop) { mCurrentResetLoop = null; }

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

        stopLoop(mCurrentResetLoop);
        stopLoop(mCurrentSortLoop);
    }
}