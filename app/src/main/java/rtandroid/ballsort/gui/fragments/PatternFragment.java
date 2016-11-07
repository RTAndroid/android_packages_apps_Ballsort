package rtandroid.ballsort.gui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import rtandroid.ballsort.R;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.gui.ColorView;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;


public class PatternFragment extends Fragment
{
    private ColorView mSelectedView = null;
    private int mOldColor = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View main = inflater.inflate(R.layout.fragment_pattern, container, false);
        GridLayout grid = (GridLayout) main.findViewById(R.id.patternGrid);
        Settings settings = SettingsManager.getSettings();
        grid.setColumnCount(Constants.PATTERN_COLUMNS_COUNT);
        grid.setRowCount(Constants.PATTERN_COLUMNS_SIZE);
        grid.removeAllViews();

        for (int col = Constants.PATTERN_COLUMNS_COUNT-1; col>=0 ; col--)
        {
            for(int row = 0; row < Constants.PATTERN_COLUMNS_SIZE; row++)
            {
                View view = inflater.inflate(R.layout.grid_view_entry, null, false);
                ColorView cv = (ColorView) view.findViewById(R.id.CV);
                cv.setColor(settings.Pattern[col][row].getPaintColor());
                cv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mSelectedView != null)
                        {
                            mSelectedView.setColor(mOldColor);
                        }
                        mOldColor = cv.getColor();
                        cv.setColor(Color.CYAN);
                        mSelectedView = cv;
                    }
                });
                grid.addView(view);
            }
        }

        return grid;
    }
}