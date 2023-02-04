package com.example.furnacethermometer.lib;

import android.os.Handler;
import android.util.Log;

import com.example.furnacethermometer.MainActivity;


public class UpdateNotificationRunnable implements Runnable {
    private static final String TAG = "UpdateNotification";
    volatile boolean stopThread = false;
    Handler mainHandler;
    RefreshTemperatureRunnableTask temperatureRefreshClass;
    MainActivity mainActivity;
    private boolean firstAlertSend = false;
    private boolean secondAlertSend = false;

    private int tLimitOne;
    private int tLimitTwo;
    private int notifTime;

    public UpdateNotificationRunnable(Handler mainHandler, RefreshTemperatureRunnableTask refreshTemperatureRunnableTask, MainActivity mainActivity, int tLimitOne, int tLimitTwo, int notifTime) {
        this.mainHandler = mainHandler;
        this.temperatureRefreshClass = refreshTemperatureRunnableTask;
        this.mainActivity = mainActivity;
        this.tLimitOne = tLimitOne;
        this.tLimitTwo = tLimitTwo;
        this.notifTime = notifTime * 1000;
    }


    @Override
    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        MainActivity.staticCreateNotification("Температура", "Температура печки " + temperatureRefreshClass.getCurrentTemperature() + " градусов", 1212, "temperature_notification", mainActivity, -1);

        Log.i(TAG, "notificationUpdateTask: Notification sent");
        checkTemperatureForNotification(temperatureRefreshClass.getCurrentTemperature());
//        MainActivity.staticCreateNotification("Температура", "Температура печки больше " + 65 + " градусов", 2222, "alert_temperature_notification", mainActivity,2);
        mainHandler.postDelayed(this, this.notifTime); // Repeat every 10 seconds
    }

    public void setStopThread() {
        stopThread = true;
    }


    public void setStartThread() {
        stopThread = false;
    }

    public void checkTemperatureForNotification(String temperature) {
        if (temperature != null) {
            int temperatureInt = Integer.parseInt(temperature);
            if (temperatureInt >= tLimitOne && !firstAlertSend) {
                firstAlertSend = true;
                Log.d(TAG, "Notification alert for temperature over 55 degrees should be sent once");
                MainActivity.staticCreateNotification("Температура", "Температура печки больше " + tLimitOne + " градусов", 2222, "alert_temperature_notification", mainActivity, 2);
            }
            if (temperatureInt < tLimitOne && firstAlertSend) {
                firstAlertSend = false;
                MainActivity.staticCreateNotification("Температура", "Температура печки меньше " + tLimitOne + " градусов", 2222, "alert_temperature_notification", mainActivity, 2);
            }
        }
        if (temperature != null) {
            int temperatureInt = Integer.parseInt(temperature);
            if (temperatureInt >= tLimitTwo && !secondAlertSend) {
                secondAlertSend = true;
                MainActivity.staticCreateNotification("Температура", "Температура печки больше " + tLimitTwo + " градусов", 2222, "alert_temperature_notification", mainActivity, 2);
            }
            if (temperatureInt < tLimitTwo && secondAlertSend) {
                secondAlertSend = false;
                MainActivity.staticCreateNotification("Температура", "Температура печки меньше " + tLimitTwo + " градусов", 2222, "alert_temperature_notification", mainActivity, 2);
            }
        }
    }
}
