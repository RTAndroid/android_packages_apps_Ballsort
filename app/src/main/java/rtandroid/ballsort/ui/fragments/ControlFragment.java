package rtandroid.ballsort.ui.fragments;

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
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;

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
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.ui.GridAdapter;

public class ControlFragment extends Fragment
{
    private static final int REFRESH_RATE_MS = 500;

    private final Handler mUiUpdateHandler = new Handler();
    private final Runnable mUiUpdateRunnable = this::updateUi;

    private ColorView mCvQueued = null;
    private ColorView mCvNext = null;
    private ColorView mCvDrop = null;
    private TextView mTvPattern = null;
    private TextView mTvBallsDropped = null;
    private TextView mTvFreeMemory = null;

    private TextView mTvFeederState = null;
    private TextView mTvSlingshotValveState = null;
    private TextView mTvSlingshotMotorState = null;

    private GridView mGridView = null;

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

        Switch mSwchSort = (Switch) view.findViewById(R.id.swSort);
        Switch mSwchReset = (Switch) view.findViewById(R.id.swReset);

        mGridView = (GridView) view.findViewById(R.id.patternGrid);


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

        mGridView.setAdapter(new GridAdapter(getActivity()));

        for (int col = Constants.PATTERN_COLUMNS_COUNT-1; col>=0 ; col--)
        {
            for(int row = 0; row < Constants.PATTERN_COLUMNS_SIZE; row++)
            {
                ColorView cv = new ColorView(getActivity());
                cv.setOnClickListener(v -> {
                    mSelectedView = cv;

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please choose a color:");
                    builder.setItems(ColorType.names, (dialog, which) -> {
                        Settings settings = SettingsManager.getSettings();
                        ColorType type = ColorType.values()[which];
                        settings.Pattern[0][0] = type;
                        mSelectedView.setColor(type.getPrimaryColor());
                        Log.d(MainActivity.TAG, "New color "+0+"  "+0+" is "+type.name());
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id) {}
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
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

        mCvQueued.setColor(data.mDetectedColor.getPrimaryColor());
        mCvNext.setColor(data.mQueuedColor.getPrimaryColor());
        mCvDrop.setColor(data.mDropColor.getPrimaryColor());

        if(!data.mModuleError.isEmpty())
        {
            mTvSlingshotValveState.setText(data.mModuleError);
            mTvSlingshotMotorState.setText(data.mModuleError);
            mTvFeederState.setText(data.mModuleError);
        }
        else
        {
            mTvSlingshotValveState.setText("Slingshot Valve: " + data.SlingshotValveState + "");
            mTvSlingshotMotorState.setText("Slingshot Motor: " + data.SlingshotMotorState + "");
            mTvFeederState.setText("Feeder state: " + data.FeederState + "");
        }


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

        mGridView.invalidateViews();

        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
    }

}