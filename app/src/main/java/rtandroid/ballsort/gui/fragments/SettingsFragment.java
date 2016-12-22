package rtandroid.ballsort.gui.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.R;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

public class SettingsFragment extends PreferenceFragment
{

    private static SettingsFragment mInstance = new SettingsFragment();

    public static SettingsFragment getInstance()
    {
        return mInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Settings settings = SettingsManager.getSettings();

        for(int i = 0; i < settings.ColumnDelaysUs.length; i++)
        {
            String prefName = "timing"+(i+1);
            String defaultValue = ""+settings.ColumnDelaysUs[i];

            Preference timingI = findPreference(prefName);
            if(timingI == null)
            {
                Log.e(MainActivity.TAG, "Could not find "+prefName);
                continue;
            }
            timingI.setTitle(defaultValue);
            timingI.setSummary(timingI.getSummary() + ". Default: "+settings.ColumnDelaysUs[i]);

            int finalI = i;
            timingI.setOnPreferenceChangeListener((preference, newValue) ->
            {
                try
                {
                    int newTime = Integer.valueOf((String)newValue);
                    Log.d(MainActivity.TAG, "New timing is "+newTime);
                    settings.ColumnDelaysUs[finalI] = newTime;
                    timingI.setTitle(""+newTime);
                }
                catch (Exception e)
                {
                    Log.d(MainActivity.TAG, "Wrong type of pref!");
                    e.printStackTrace();
                    return false;
                }
                return true;
            });
        }

        String numReadsName = "colormeassurments";
        String defaultValue = ""+settings.ColorSersorRepeats;
        Preference pref = findPreference(numReadsName);
        if(pref == null)
        {
            Log.e(MainActivity.TAG, "Could not find "+numReadsName);
        }
        pref.setTitle(defaultValue);
        pref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                int newTime = Integer.valueOf((String)newValue);
                Log.d(MainActivity.TAG, "New timing is "+newTime);
                settings.ColorSersorRepeats = newTime;
                pref.setTitle(""+newTime);
            }
            catch (Exception e)
            {
                Log.d(MainActivity.TAG, "Wrong type of pref!");
                e.printStackTrace();
                return false;
            }
            return true;
        });

        String detectionName = "colordetection";
        defaultValue = ""+settings.ColorDetection;
        Preference detPref = findPreference(detectionName);
        if(detPref == null)
        {
            Log.e(MainActivity.TAG, "Could not find "+detectionName);
        }
        detPref.setTitle(defaultValue);
        detPref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                int newTime = Integer.valueOf((String)newValue);
                Log.d(MainActivity.TAG, "New timing is "+newTime);
                settings.ColorSersorRepeats = newTime;
                detPref.setTitle(""+newTime);
            }
            catch (Exception e)
            {
                Log.d(MainActivity.TAG, "Wrong type of pref!");
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }
}