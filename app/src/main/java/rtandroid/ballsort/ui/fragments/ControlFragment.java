package rtandroid.ballsort.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.ui.ColorView;
import rtandroid.ballsort.services.ResetService;
import rtandroid.ballsort.services.SortService;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.ui.GridAdapter;

public class ControlFragment extends Fragment
{
    private static final int REFRESH_RATE_MS = 500;

    private final Handler mUiUpdateHandler = new Handler();
    private final Runnable mUiUpdateRunnable = this::updateUi;

    private static Intent sSortIntent = null;
    private static Intent sResetIntent = null;

    private ColorView mCvDetected = null;
    private ColorView mCvQueued = null;
    private ColorView mCvDrop = null;
    private TextView mTvPattern = null;
    private TextView mTvBallsDropped = null;
    private TextView mTvFreeMemory = null;

    private TextView mTvFeederState = null;
    private TextView mTvSlingshotValveState = null;
    private TextView mTvSlingshotMotorState = null;

    private ColorView mSelectedView = null;
    private GridView mGridView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        mCvDetected = (ColorView) view.findViewById(R.id.cvDetectedBall);
        mCvQueued = (ColorView) view.findViewById(R.id.cvQueuedBall);
        mCvDrop = (ColorView) view.findViewById(R.id.cvDropBall);
        mTvPattern = (TextView) view.findViewById(R.id.tvPatternCount);
        mTvBallsDropped = (TextView) view.findViewById(R.id.tvBallsDropped);
        mTvFreeMemory = (TextView) view.findViewById(R.id.tvFreeMemory);
        mTvFeederState = (TextView) view.findViewById(R.id.tvFeederState);
        mTvSlingshotValveState = (TextView) view.findViewById(R.id.tvSlingshotValveState);
        mTvSlingshotMotorState = (TextView) view.findViewById(R.id.tvSlingshotMotorState);
        mGridView = (GridView) view.findViewById(R.id.patternGrid);

        Context context = getActivity();
        GridAdapter adapter = new GridAdapter(context);
        mGridView.setAdapter(adapter);

        ToggleButton btnSort = (ToggleButton) view.findViewById(R.id.btnSort);
        btnSort.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                getActivity().startService(sSortIntent);
            }
            else
            {
                getActivity().stopService(sSortIntent);
            }
        });

        ToggleButton btnReset = (ToggleButton) view.findViewById(R.id.btnReset);
        btnReset.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                getActivity().startService(sResetIntent);
            }
            else
            {
                getActivity().stopService(sResetIntent);
            }
        });

        for (int col = Constants.PATTERN_COLUMN_COUNT - 1; col >= 0 ; col--)
            for (int row = 0; row < Constants.PATTERN_COLUMN_CAPACITY; row++)
            {
                ColorView cv = new ColorView(context);
                cv.setOnClickListener(v ->
                {
                    mSelectedView = cv;

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setNegativeButton("Cancel", (dialog, id) -> {});
                    builder.setTitle("Please choose a color:");
                    builder.setItems(ColorType.names, (dialog, which) ->
                    {
                        ColorType type = ColorType.values()[which];
                        SettingsManager.getSettings().Pattern[0][0] = type;
                        Log.d(MainActivity.TAG, "New color is " + type.name());
                        mSelectedView.setOuterColor(type.getColor());
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }

        // intents start and stop both services
        sSortIntent = new Intent(context, SortService.class);
        sResetIntent = new Intent(context, ResetService.class);

        // create two temporary loops to initialise the needed hardware
        Log.i(MainActivity.TAG, "Initialising hardware state...");
        new SortLoop();
        new ResetLoop();

        Log.i(MainActivity.TAG, "Starting UI updates...");
        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
        return view;
    }

    private void updateUi()
    {
        DataState data = SettingsManager.getData();

        int detected = data.mDetectedColor.getColor();
        mCvDetected.setInnerColor(detected);
        mCvDetected.setOuterColor(detected);

        int next = data.mQueuedColor.getColor();
        mCvQueued.setInnerColor(next);
        mCvQueued.setOuterColor(next);

        int drop = data.mDropColor.getColor();
        mCvDrop.setInnerColor(drop);
        mCvDrop.setOuterColor(drop);

        mTvFeederState.setText("Feeder: " + data.FeederState + "");
        mTvSlingshotValveState.setText("Slingshot Valve: " + data.SlingshotValveState + "");
        mTvSlingshotMotorState.setText("Slingshot Motor: " + data.SlingshotMotorState + "");

        String patternString = "";
        for (int i = 1; i <= data.mFillings.length; i++) { patternString += data.mFillings[data.mFillings.length-i] + " "; }
        mTvPattern.setText("Pattern: " + patternString + "");

        Runtime rt = Runtime.getRuntime();
        long freeKB = rt.freeMemory() / 1024;
        long totalKB = rt.totalMemory() / 1024;
        mTvFreeMemory.setText("Free memory: " + freeKB + " kb / " + totalKB + " kb");
        mTvBallsDropped.setText("Dropped: " + data.mDetectedBalls + " balls");

        mGridView.invalidateViews();
        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }
}