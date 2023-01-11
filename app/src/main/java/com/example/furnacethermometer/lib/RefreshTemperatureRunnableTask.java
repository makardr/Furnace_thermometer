package com.example.furnacethermometer.lib;

import android.os.Handler;
import android.util.Log;


//  This class is runnable that was separated into its own class to better separate all the background things connected to the
//  background task into one place, and to allow better control of it

public class RefreshTemperatureRunnableTask implements Runnable {
    private static final String TAG = "RefreshTemperatureTask";
    Handler backgroundHandler;
    volatile boolean stopThread;
    String currentTemperature = "0";
    GetThermometerValue thermometerValue;

    public RefreshTemperatureRunnableTask(Handler backgroundHandler, String ipAddress) {
        this.backgroundHandler = backgroundHandler;
        this.stopThread = false;
        this.thermometerValue = new GetThermometerValue(ipAddress);
    }

    @Override
    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        backgroundTask();
        backgroundHandler.postDelayed(this, 5000); // Repeat every 3 seconds
    }

    public void backgroundTask() {
        String response = thermometerValue.readStringHtml();
        Log.d(TAG, "Thread " + Thread.currentThread().getId() + " background task executed, result is " + response);
        currentTemperature = response;
    }

    public void setStopThread() {
        stopThread = true;
    }

    public void setStartThread() {
        stopThread = false;
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }

}
