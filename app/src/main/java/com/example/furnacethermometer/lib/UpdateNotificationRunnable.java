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
    private boolean messageSend = false;
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
        MainActivity.staticCreateNotification("Температура", "Температура печки " + temperatureRefreshClass.getCurrentTemperature() + " градусов", 1212, "temperature_notification", mainActivity,-1);

        Log.i(TAG, "notificationUpdateTask: Notification sent");
        checkTemperatureForNotification(temperatureRefreshClass.getCurrentTemperature());
//        MainActivity.staticCreateNotification("Температура", "Температура печки больше " + 65 + " градусов", 2222, "alert_temperature_notification", mainActivity,2);
        mainHandler.postDelayed(this, 10000); // Repeat every 10 seconds
    }

    public void setStopThread() {
        stopThread = true;
    }


    public void setStartThread() {
        stopThread = false;
    }

    public void checkTemperatureForNotification(String temperature) {
        int temperatureLimit = 65;
        if (temperature != null) {
            int temperatureInt = Integer.parseInt(temperature);
            if (temperatureInt >= temperatureLimit && !messageSend) {
                messageSend = true;
                Log.d(TAG, "Notification alert for temperature over 50 degrees should be sent once");
                MainActivity.staticCreateNotification("Температура", "Температура печки больше " + temperatureLimit + " градусов", 2222, "alert_temperature_notification", mainActivity, 2);
            }
            if (temperatureInt < temperatureLimit && messageSend) {
                messageSend = false;
                Log.d(TAG, "Flag is false");
            }
        }
    }
}
