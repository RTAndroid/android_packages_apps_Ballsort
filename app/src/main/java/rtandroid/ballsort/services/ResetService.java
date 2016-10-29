package rtandroid.ballsort.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.loops.ResetLoop;
import rtandroid.ballsort.blocks.loops.SortLoop;
import rtandroid.ballsort.hardware.Sorter;
import rtandroid.ballsort.util.Utils;


public class ResetService extends Service
{
    private ResetLoop mCurrentResettLoop;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(MainActivity.TAG, "Starting the main execution");
        // system setup
        Sorter.extract(this);
        Sorter.load();

        mCurrentResettLoop = new ResetLoop();
        Utils.lockPower();
        mCurrentResettLoop.start();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.i(MainActivity.TAG, "Stopping the main execution");
        if (mCurrentResettLoop == null) { return; }
        mCurrentResettLoop.terminate();
        mCurrentResettLoop.waitFor();

        Utils.unlockPower();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
