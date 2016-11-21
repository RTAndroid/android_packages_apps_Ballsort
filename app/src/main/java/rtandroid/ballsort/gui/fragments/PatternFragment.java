package rtandroid.ballsort.gui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.gui.ColorView;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;


public class PatternFragment extends Fragment
{
    private ColorView mSelectedView = null;

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
                grid.addView(view);
            }
        }

        return grid;
    }
}