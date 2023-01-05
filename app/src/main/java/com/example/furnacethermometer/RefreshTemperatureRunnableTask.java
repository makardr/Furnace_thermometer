package com.example.furnacethermometer;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


//  This class is runnable that was separated into its own class to better separate all the background things connected to the
//  background task into one place, and to allow better control of it

public class RefreshTemperatureRunnableTask implements Runnable {
    private static final String TAG = "RefreshTemperatureTask";
    Handler backgroundHandler;
    volatile boolean stopThread;
    String currentTemperature="0";
    GetThermometerValue thermometerValue;

    RefreshTemperatureRunnableTask(Handler backgroundHandler) {
        this.backgroundHandler = backgroundHandler;
        this.stopThread = false;

        thermometerValue = new GetThermometerValue("http://192.168.0.120/");
    }

    @Override
    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        backgroundTask();
        backgroundHandler.postDelayed(this, 3000); // Repeat every 3 seconds
    }

    public void backgroundTask() {
        String response = thermometerValue.readStringHtml();
        Log.i(TAG, "Response thermometer value is " + response);
        currentTemperature=response;
    }

    public void setStopThread(){
        stopThread=true;
    }
    public void setStartThread(){
        stopThread=false;
    }
    public String getCurrentTemperature(){
        return currentTemperature;
    }

}
