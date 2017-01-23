package rtandroid.ballsort.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

import rtandroid.ballsort.ui.fragments.ControlFragment;
import rtandroid.ballsort.ui.fragments.LearningFragment;
import rtandroid.ballsort.ui.fragments.SettingsFragment;


public class FragmentAdapter extends FragmentPagerAdapter
{
    private final String[] tabTitles = new String[] { "Control", "Settings", "Learning"};

    public FragmentAdapter(FragmentManager fm, Context context)
    {
        super(fm);
    }

    @Override
    public int getCount()
    {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position){
            case 0: return new ControlFragment();
            case 1: return new SettingsFragment();
            case 2: return new LearningFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }
}
