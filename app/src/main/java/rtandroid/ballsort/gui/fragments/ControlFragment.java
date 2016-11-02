package rtandroid.ballsort.gui.fragments;

import android.content.Intent;
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
import rtandroid.ballsort.services.ResetService;
import rtandroid.ballsort.services.SortService;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class ControlFragment extends Fragment
{
    private static final int REFRESH_RATE_MS = 500;

    private final Handler mUiUpdateHandler = new Handler();
    private final Runnable mUiUpdateRunnable = this::updateUi;

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

    private static Intent mSortIntent = null;
    private static Intent mResetIntent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        mCvQueued = (ColorView) view.findViewById(R.id.cvQueuedBall);
        mCvNext = (ColorView) view.findViewById(R.id.cvNextBall);
        mCvDrop = (ColorView) view.findViewById(R.id.cvDropBall);

        mTvBallsDropped = (TextView) view.findViewById(R.id.tvBallsDropped);
        mTvFreeMemory = (TextView) view.findViewById(R.id.tvFreeMemory);
        mTvFeederState = (TextView) view.findViewById(R.id.tvFeederState);
        mTvSlingshotValveState = (TextView) view.findViewById(R.id.tvSlingshotValveState);
        mTvSlingshotMotorState = (TextView) view.findViewById(R.id.tvSlingshotMotorState);

        mSwchSort = (Switch) view.findViewById(R.id.swSort);
        mSwchReset = (Switch) view.findViewById(R.id.swReset);


        mSwchSort.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                getActivity().startService(mSortIntent);
            }
            else
            {
                getActivity().stopService(mSortIntent);
            }
        });

        mSwchReset.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                getActivity().startService(mResetIntent);
            }
            else
            {
                getActivity().stopService(mResetIntent);
            }
        });

        // create two temporary loops to initialise the needed hardware
        Log.i(MainActivity.TAG, "Initialising hardware state...");
        new SortLoop();
        new ResetLoop();

        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSortIntent = new Intent(getActivity(), SortService.class);
        mResetIntent = new Intent(getActivity(), ResetService.class);
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

        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }

}