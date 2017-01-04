package rtandroid.ballsort.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;


public class BallsortFragmentPagerAdapter extends FragmentPagerAdapter
{
    private final String[] tabTitles = new String[] { "Control", "Settings", "Learning"};

    public BallsortFragmentPagerAdapter(FragmentManager fm, Context context)
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
