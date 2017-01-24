package rtandroid.ballsort.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

public class GridAdapter extends BaseAdapter
{
    private final Context mContext;

    public GridAdapter(Context c)
    {
        mContext = c;
    }

    public int getCount()
    {
        return Constants.PATTERN_COLUMN_COUNT * Constants.PATTERN_COLUMN_CAPACITY;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();

        int col = Constants.PATTERN_COLUMN_COUNT    - 1 - (position % Constants.PATTERN_COLUMN_COUNT);
        int row = Constants.PATTERN_COLUMN_CAPACITY - 1 - (position / Constants.PATTERN_COLUMN_COUNT);

        ColorView cv;
        if (convertView != null) { cv = (ColorView) convertView; }
        else
        {
            int size = Constants.PATTERN_IMAGE_SIZE;
            GridView.LayoutParams lp = new GridView.LayoutParams(size, size);

            cv = new ColorView(mContext);
            cv.setLayoutParams(lp);
        }

        cv.setOnClickListener(v ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setNegativeButton("Cancel", (dialog, id) -> {});
            builder.setTitle("Please select new color:");
            builder.setItems(ColorType.names, (dialog, which) ->
            {
                ColorType type = ColorType.values()[which];
                settings.Pattern[col][row] = type;
                Log.d(MainActivity.TAG, "New color in (" + col + ", " + row + "): " + type.name());
                cv.setOuterColor(type.getColor());
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // outer color shows which color is expected in this spot
        ColorType type = settings.Pattern[col][row];
        int outerColor = type.getColor();
        cv.setOuterColor(type.getColor());

        // inner color shows whether the ball was already sorted
        boolean filled = (data.mFillings[col] - 1 >= row);
        int innerColor = filled ? outerColor : ColorType.EMPTY.getColor() ;
        cv.setInnerColor(innerColor);

        return cv;
    }
}