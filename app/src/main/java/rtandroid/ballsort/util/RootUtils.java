package rtandroid.ballsort.util;

import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.root.PrivilegeElevator;

public class RootUtils
{
    public static void enableRoot()
    {
        try
        {
            PrivilegeElevator.enableRoot();
        }
        catch (Error | Exception e)
        {
            Log.e(MainActivity.TAG, "Could not acquire root / " + e.getMessage());
        }
    }

    public static void disableRoot()
    {
        try
        {
            PrivilegeElevator.disableRoot();
        }
        catch (Error | Exception e)
        {
            Log.e(MainActivity.TAG, "Could not release root / " + e.getMessage());
        }
    }
}
