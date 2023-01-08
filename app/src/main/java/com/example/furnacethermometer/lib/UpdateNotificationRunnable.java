package com.example.furnacethermometer.lib;

import android.util.Log;
import com.example.furnacethermometer.MainActivity;

import android.os.Handler;


public class UpdateNotificationRunnable implements Runnable {
    private static final String TAG = "UpdateNotification";
    volatile boolean stopThread = false;
    Handler mainHandler;
    RefreshTemperatureRunnableTask temperatureRefreshClass;
    MainActivity mainActivity;

    public UpdateNotificationRunnable(Handler mainHandler, RefreshTemperatureRunnableTask refreshTemperatureRunnableTask, MainActivity mainActivity) {
        this.mainHandler = mainHandler;
        this.temperatureRefreshClass = refreshTemperatureRunnableTask;
        this.mainActivity = mainActivity;
    }


    @Override
    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        MainActivity.staticCreateNotification("Температура", "Температура печки  " + temperatureRefreshClass.getCurrentTemperature() + " градусов", 1212, "temperature_notification", mainActivity);
        Log.i(TAG, "notificationUpdateTask: Notification sent");
        mainHandler.postDelayed(this, 10000); // Repeat every 10 seconds
    }

    public void setStopThread() {
        stopThread = true;
    }


    public void setStartThread() {
        stopThread = false;
    }

}
