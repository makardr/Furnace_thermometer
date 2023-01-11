package com.example.furnacethermometer.lib;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.furnacethermometer.MainActivity;

public class UpdateInterfaceRunnable implements Runnable {
    private static final String TAG = "UpdateInterfaceRunnable";
    volatile boolean stopThread = false;
    Handler mainHandler;
    RefreshTemperatureRunnableTask temperatureRefreshClass;
    TextView tv;
    int interfaceRefresh;

    public UpdateInterfaceRunnable(TextView tv, Handler mainHandler, RefreshTemperatureRunnableTask refreshTemperatureRunnableTask, int interfaceRefresh) {
        this.mainHandler = mainHandler;
        this.temperatureRefreshClass = refreshTemperatureRunnableTask;
        this.tv = tv;
        this.interfaceRefresh = interfaceRefresh * 1000;
    }


    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        MainActivity.changeTV(tv, temperatureRefreshClass.getCurrentTemperature());
        mainHandler.postDelayed(this, interfaceRefresh); // Repeat every 1 second


    }

    public void setStopThread() {
        stopThread = true;
    }


    public void setStartThread() {
        stopThread = false;
    }
}
