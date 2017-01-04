package rtandroid.ballsort.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.blocks.color.classifier.NeuralColorClassifier;
import rtandroid.ballsort.blocks.loops.LearningLoop;
import rtandroid.ballsort.services.ResetService;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;

public class LearningFragment extends Fragment
{
    private static final int REFRESH_RATE_MS = 500;

    private final Handler mUiUpdateHandler = new Handler();
    private final Runnable mUiUpdateRunnable = this::updateUi;

    private LearningLoop mCurrentLearningLoop = null;

    private Switch mSwchStart = null;
    private Switch mSwchTrain = null;
    private TextView mTvErr = null;

    private static Intent mResetIntent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_learning, container, false);

        mSwchStart = (Switch) view.findViewById(R.id.swStart);
        Switch mSwchReset = (Switch) view.findViewById(R.id.swReset);
        mSwchTrain = (Switch) view.findViewById(R.id.swTrain);
        mTvErr = (TextView) view.findViewById(R.id.tvTrainErr);

        mSwchStart.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                Log.i(MainActivity.TAG, "Starting the LearningLoop execution");
                mCurrentLearningLoop = new LearningLoop();
                mCurrentLearningLoop.start();
            }
            else
            {
                Log.i(MainActivity.TAG, "Stopping the LearningLoop execution");
                if (mCurrentLearningLoop == null) { return; }
                mCurrentLearningLoop.terminate();
                mCurrentLearningLoop.waitFor();
                mCurrentLearningLoop = null;
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

        mSwchTrain.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                NeuralColorClassifier.getInstance().startLearning();
                mSwchTrain.setActivated(false);
            }
        });


        mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mResetIntent = new Intent(getActivity(), ResetService.class);
    }

    private void updateUi()
    {
        DataState data = SettingsManager.getData();

        if(data.mLatestColor != null && mCurrentLearningLoop != null)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Please choose a color:");
            builder.setItems(ColorType.names, (dialog, which) ->
            {
                ColorType type = ColorType.values()[which];
                Log.d(MainActivity.TAG, "New color is "+type.name());
                NeuralColorClassifier.getInstance().addTrainingsEntry(type, data.mLatestColor);
                data.mLatestColor = null;
                mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
            });

            builder.setNegativeButton("Cancel", (dialog, id) ->
            {
                mSwchStart.setChecked(false);
                data.mLatestColor = null;
                mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
            });

            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else
        {
            mTvErr.setText("Error: " + data.mLearningError);
            mUiUpdateHandler.postDelayed(mUiUpdateRunnable, REFRESH_RATE_MS);
        }
    }

}