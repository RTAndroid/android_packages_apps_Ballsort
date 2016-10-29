package rtandroid.ballsort.gui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;


public class BallsortFragmentPagerAdapter extends FragmentPagerAdapter
{
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Control", "Settings"};

    public BallsortFragmentPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position)
    {
        if(position == 0)
        {
            return new ControlFragment();
        }
        else
        {
            return new SettingsFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return "page "+position;
    }
}
