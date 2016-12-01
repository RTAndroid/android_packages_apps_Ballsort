package rtandroid.ballsort.gui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.blocks.AStateBlock;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.gui.ColorView;
import rtandroid.ballsort.services.ResetService;
import rtandroid.ballsort.services.SortService;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
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
    private TextView mTvPattern = null;
    private TextView mTvBallsDropped = null;
    private TextView mTvFreeMemory = null;

    private TextView mTvFeederState = null;
    private TextView mTvSlingshotValveState = null;
    private TextView mTvSlingshotMotorState = null;

    private static Intent mSortIntent = null;
    private static Intent mResetIntent = null;

    private ColorView mSelectedView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        mCvQueued = (ColorView) view.findViewById(R.id.cvQueuedBall);
        mCvNext = (ColorView) view.findViewById(R.id.cvNextBall);
        mCvDrop = (ColorView) view.findViewById(R.id.cvDropBall);

        mTvPattern = (TextView) view.findViewById(R.id.tvPatternCount);

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

        GridLayout grid = (GridLayout) view.findViewById(R.id.patternGrid);
        Settings settings = SettingsManager.getSettings();
        grid.setColumnCount(Constants.PATTERN_COLUMNS_COUNT);
        grid.setRowCount(Constants.PATTERN_COLUMNS_SIZE);
        grid.removeAllViews();

        for (int col = Constants.PATTERN_COLUMNS_COUNT-1; col>=0 ; col--)
        {
            for(int row = 0; row < Constants.PATTERN_COLUMNS_SIZE; row++)
            {
                View gview = inflater.inflate(R.layout.grid_view_entry, null, false);
                ColorView cv = (ColorView) gview.findViewById(R.id.CV);
                cv.setColor(settings.Pattern[col][row].getPaintColor());
                int finalRow = Constants.PATTERN_COLUMNS_SIZE-row-1;
                int finalCol = col;
                cv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mSelectedView = cv;

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Please choose a color:");
                        builder.setItems(ColorType.names, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Settings settings = SettingsManager.getSettings();
                                ColorType type = ColorType.values()[which];
                                settings.Pattern[finalCol][finalRow] = type;
                                mSelectedView.setColor(type.getPaintColor());
                                Log.d(MainActivity.TAG, "New color "+finalCol+"  "+finalRow+" is "+type.name());
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {}
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                grid.addView(gview);
            }
        }


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

        String patternString = "";
        for(int i = 1; i <= data.mFillings.length; i++)
        {
            patternString += data.mFillings[data.mFillings.length-i]+ " ";
        }
        mTvPattern.setText("Pattern: "+ patternString);

        Runtime rt = Runtime.getRuntime();
        long freeKB = rt.freeMemory() / 1024;
        long totalKB = rt.totalMemory() / 1024;
        mTvFreeMemory.setText("Free memory: " + freeKB + " kb / " + totalKB + " kb");
        mTvBallsDropped.setText("Dropped: " + data.mDetectedBalls + " balls");

        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }

}