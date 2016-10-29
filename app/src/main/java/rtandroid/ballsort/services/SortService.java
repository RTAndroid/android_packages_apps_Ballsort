package rtandroid.ballsort.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.hardware.Sorter;
import rtandroid.ballsort.util.Utils;


public class SortService extends Service
{

    private SortLoop mCurrentSortLoop;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(MainActivity.TAG, "Starting the main execution");
        // system setup
        Sorter.extract(this);
        Sorter.load();

        mCurrentSortLoop = new SortLoop();
        Utils.lockPower();
        mCurrentSortLoop.start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.i(MainActivity.TAG, "Stopping the main execution");
        if (mCurrentSortLoop == null) { return; }
        mCurrentSortLoop.terminate();
        mCurrentSortLoop.waitFor();

        // system setup
        Sorter.unload();
        Utils.unlockPower();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
