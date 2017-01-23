package rtandroid.ballsort.ui;

import android.app.AlertDialog;
import android.content.Context;
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
        return Constants.PATTERN_COLUMNS_COUNT * Constants.PATTERN_COLUMNS_SIZE;
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

        ColorView cv;
        if (convertView == null)
        {
            cv = new ColorView(mContext);
            cv.setLayoutParams(new GridView.LayoutParams(60, 60));
            cv.setPadding(8, 8, 8, 8);
        }
        else
        {
            cv = (ColorView) convertView;
        }

        int col = Constants.PATTERN_COLUMNS_COUNT - 1 - (position % Constants.PATTERN_COLUMNS_COUNT);
        int row = Constants.PATTERN_COLUMNS_SIZE - 1 - (position / Constants.PATTERN_COLUMNS_COUNT);

        cv.setOnClickListener(v ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setNegativeButton("Cancel", (dialog, id) -> {});
            builder.setTitle("Please choose a color:");
            builder.setItems(ColorType.names, (dialog, which) ->
            {
                Settings settings1 = SettingsManager.getSettings();
                ColorType type = ColorType.values()[which];
                settings1.Pattern[col][row] = type;
                cv.setColor(type.getPrimaryColor());
                Log.d(MainActivity.TAG, "New color "+col+"  "+row+" is "+type.name());
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        if (data.mFillings[col]-1 >= row)
        {
            cv.setColor(settings.Pattern[col][row].getPrimaryColor());
        }
        else
        {
            cv.setColor(settings.Pattern[col][row].getSecondaryColor());
        }

        return cv;
    }
}