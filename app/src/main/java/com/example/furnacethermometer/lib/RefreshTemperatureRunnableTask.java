package com.example.furnacethermometer.lib;

import android.os.Handler;
import android.util.Log;

import com.example.furnacethermometer.MainActivity;


//  This class is runnable that was separated into its own class to better separate all the background things connected to the
//  background task into one place, and to allow better control of it

public class RefreshTemperatureRunnableTask implements Runnable {
    private static final String TAG = "RefreshTemperatureTask";
    Handler backgroundHandler;
    volatile boolean stopThread;
    String currentTemperature = "0";
    GetThermometerValue thermometerValue;
    int refreshTime;

    public RefreshTemperatureRunnableTask(Handler backgroundHandler, String ipAddress, int refreshTime) {
        this.backgroundHandler = backgroundHandler;
        this.stopThread = false;
//        Log.d(TAG, ipAddress);
        this.thermometerValue = new GetThermometerValue(ipAddress);
        this.refreshTime = refreshTime * 1000;
    }

    @Override
    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        backgroundTask();
        backgroundHandler.postDelayed(this, refreshTime); // Repeat every x seconds
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
