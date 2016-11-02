package rtandroid.ballsort.gui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.settings.SettingsManager;

public class SettingsFragment extends Fragment
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v ->
        {
            SettingsManager.writeToFile();
            Log.i(MainActivity.TAG, "Settings saved");
        });

        Button btnLoad = (Button) view.findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(v ->
        {
            SettingsManager.readFromFile();
            Log.i(MainActivity.TAG, "Settings loaded");
        });
        return view;
    }
}