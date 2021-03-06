package rtandroid.ballsort.ui.fragments;

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

        String ColorYellowThreshold = "ColorYellowThreshold";
        String defaultValue = "" + settings.ColorYellowThreshold;
        Preference ColorYellowThresholdPref = findPreference(ColorYellowThreshold);
        if (ColorYellowThresholdPref == null)
        {
            Log.e(MainActivity.TAG, "Could not find "+ColorYellowThreshold);
            return;
        }

        ColorYellowThresholdPref.setTitle(defaultValue);
        ColorYellowThresholdPref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                int newTime = Integer.valueOf((String)newValue);
                Log.d(MainActivity.TAG, "New timing is "+newTime);
                settings.ColorYellowThreshold = newTime;
                ColorYellowThresholdPref.setTitle(""+newTime);
            }
            catch (Exception e)
            {
                Log.d(MainActivity.TAG, "Wrong type of pref!");
                e.printStackTrace();
                return false;
            }
            return true;
        });

        String ColorBlackThreshold = "ColorBlackThreshold";
        defaultValue = ""+settings.ColorBlackThreshold;
        Preference ColorBlackThresholdPref = findPreference(ColorBlackThreshold);
        if (ColorBlackThresholdPref == null)
        {
            Log.e(MainActivity.TAG, "Could not find " + ColorBlackThreshold);
            return;
        }

        ColorBlackThresholdPref.setTitle(defaultValue);
        ColorBlackThresholdPref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                int newTime = Integer.valueOf((String)newValue);
                Log.d(MainActivity.TAG, "New timing is "+newTime);
                settings.ColorBlackThreshold = newTime;
                ColorBlackThresholdPref.setTitle(""+newTime);
            }
            catch (Exception e)
            {
                Log.d(MainActivity.TAG, "Wrong type of pref!");
                e.printStackTrace();
                return false;
            }
            return true;
        });

        String ColorLightColorThreshold = "ColorLightColorThreshold";
        defaultValue = ""+settings.ColorYellowThreshold;
        Preference ColorLightColorThresholdPref = findPreference(ColorLightColorThreshold);
        if (ColorLightColorThresholdPref == null)
        {
            Log.e(MainActivity.TAG, "Could not find "+ColorLightColorThreshold);
            return;
        }

        ColorLightColorThresholdPref.setTitle(defaultValue);
        ColorLightColorThresholdPref.setOnPreferenceChangeListener((preference, newValue) ->
        {
            try
            {
                int newTime = Integer.valueOf((String)newValue);
                Log.d(MainActivity.TAG, "New timing is "+newTime);
                settings.ColorLightColorThreshold = newTime;
                ColorLightColorThresholdPref.setTitle(""+newTime);
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